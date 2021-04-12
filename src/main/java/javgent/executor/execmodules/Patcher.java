package javgent.executor.execmodules;

import javgent.executor.model.PatchClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Runs the patch, restructures the code
 */
public class Patcher {

    private static final Logger Log = LoggerFactory.getLogger(Patcher.class);

    private PatcherConfig config;

    private Map<String, PatchClass> patchClassMap;
    private Set<ClassInfo> classSet;

    private Set<FileEntryInfo> returnSet;

    public Patcher(PatcherConfig patcherConfig) {
        this.config = patcherConfig;
        this.classSet = this.config.classInfos;
    }

    public Set<FileEntryInfo> run() {
        Objects.requireNonNull(this.config);

        Log.info("Starting");

        createPatchClassMap();

        byteCodeModify();

        renameClasses();

        Log.info("Done");

        return returnSet;
    }


    private void createPatchClassMap() {
        this.patchClassMap = this.config.classPatchFiles.stream()
                .collect(
                        Collectors.toMap(
                                pc -> pc.ObfName,
                                pc -> pc
                        )
                );
    }

    private void byteCodeModify() {
        Log.info("Starting ByteCode-Modification");
        StopWatch sw = StopWatch.createStarted();

        var byteCodeModifierConfig = new ByteCodeModifierConfig();
        byteCodeModifierConfig.classSet = this.classSet;
        byteCodeModifierConfig.patchClassesMap = this.patchClassMap;
        byteCodeModifierConfig.modifiedClassesDir = this.config.modifiedClassesDir;
        byteCodeModifierConfig.inMemory = this.config.inMemory;
        this.returnSet = new ByteCodeModifier(byteCodeModifierConfig).run();

        sw.stop();
        Log.info("Finished ByteCode-Modification, took {}ms", sw.getTime());
    }

    private void renameClasses() {
        Log.info("Starting renaming/moving classes");
        StopWatch sw = StopWatch.createStarted();

        new HashSet<>(returnSet).forEach(this::renameClass);

        sw.stop();
        Log.info("Finished renaming/moving classes, took {}ms", sw.getTime());
    }

    private void renameClass(FileEntryInfo fi) {

        var oldNamePath = fi.RelativeNamePath;

        var newName = FilenameUtils.getBaseName(fi.RelativeNamePath);
        var patchClass = patchClassMap.get(newName);
        if (patchClass == null) {
            Log.debug("Failed to find patchClass for '{}'", newName);
            return;
        }

        fi.RelativeNamePath = patchClass.Name + ".class";


        if(config.inMemory) {
            var parentPath = FilenameUtils.getPath(fi.RelativeNamePath);

            if(returnSet.stream()
                    .filter(file -> file.RelativeNamePath.equals(parentPath))
                    .findAny().isEmpty()) {
                var parentFei = new FileEntryInfo();
                parentFei.RelativeNamePath = parentPath;
                parentFei.IsDirectory = true;

                returnSet.add(parentFei);
            }
        } else {
            var srcPath = this.config.modifiedClassesDir.resolve(oldNamePath);

            var newPath = this.config.modifiedClassesDir.resolve(patchClass.Name + ".class");
            Log.debug("Moving '{}'->'{}'", srcPath, newPath);

            try {
                newPath.toFile().getParentFile().mkdirs();
                Files.move(srcPath, newPath);
            } catch (IOException e) {
                Log.error("Failed to move file", e);
            }
        }
    }

}
