package javgent.executor.execmodules;

import javgent.executor.CancelExecutionException;
import javgent.executor.execmodules.jarpacker.DiskJarPacker;
import javgent.executor.execmodules.jarpacker.InMemoryJarPacker;
import javgent.executor.execmodules.patchfiles.ComJsonDirPatchFileReader;
import javgent.executor.execmodules.patchfiles.NativeMappingsProcessor;
import javgent.executor.model.PatchClass;
import javgent.util.DirUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main processor
 */
public class Executor {

    private static final Logger Log = LoggerFactory.getLogger(Executor.class);

    private ExecutorConfig config;
    private MappingMode mappingMode;

    private Path inputDir;
    private Path patchFileDir;
    private Path classesDir;
    private Path nonClassesDir;
    private Path modifiedClassesDir;

    private Path srcJar;

    private Set<PatchClass> patchClassesSet = new HashSet<>();

    private Set<ClassInfo> classInfos = new HashSet<>();

    private Set<ExtractedJarFileEntryInfo> jarEntrys;
    private Set<FileEntryInfo> modifiedClasses;

    public Executor(ExecutorConfig config) {
        Objects.requireNonNull(config);
        this.config = config;

        mappingMode = config.patchFileDir != null ? MappingMode.JSON_COM : MappingMode.MAPPING_FILE;

        if (!config.inMemoryMode) {
            this.inputDir = Paths.get(config.workDir, "input");

            if (mappingMode == MappingMode.JSON_COM)
                this.patchFileDir = Paths.get(config.workDir, "patchFiles");

            this.classesDir = Paths.get(config.workDir, "classes");
            this.nonClassesDir = Paths.get(config.workDir, "nonclasses");

            this.modifiedClassesDir = Paths.get(config.workDir, "modified_classes");
        } else {
            config.readInputDirectly = true;
        }

    }

    public void run() {

        var sw = StopWatch.createStarted();

        prepare();

        preparePopulateDirs();

        var taskList = new ArrayList<CompletableFuture<Void>>();
        taskList.add(CompletableFuture.runAsync(this::readPatchFiles));
        taskList.add(CompletableFuture.runAsync(this::unpackJAR));

        Log.info("Waiting for Tasks to finish...");
        for (var task : taskList) {
            task.join();
        }

        if (!config.inMemoryMode && mappingMode == MappingMode.JSON_COM)
            preLoadClassesFromDisk();

        patchClassFiles();

        packJar();

        sw.stop();
        Log.info("Done, took {} (or {}ms)", DurationFormatUtils.formatDurationHMS(sw.getTime()), sw.getTime());
    }

    private void prepare() {
        Log.info("Preparing...");

        if (config.inMemoryMode) {
            Log.info("Running in memory mode!");
        } else {
            if (config.cleanWorkDir) {
                Log.info("Cleaning workdir");
                DirUtil.ensureCreatedAndEmpty(Paths.get(this.config.workDir));
            } else
                DirUtil.ensureCreated(Paths.get(this.config.workDir));
        }

        Log.info("Preparing finished");
    }

    private void preparePopulateDirs() {

        Log.info("Preparing: Populating dirs");

        var srcPath = Paths.get(config.srcFile);

        if (config.readInputDirectly) {
            this.srcJar = srcPath;
            Log.info("Set srcJar='{}'", this.srcJar);
        } else {
            //Copy srcJar
            DirUtil.ensureCreatedAndEmpty(this.inputDir);

            this.srcJar = inputDir.resolve(srcPath.toFile().getName());

            Log.info("Copying srcJar: '{}'->'{}'", srcPath, this.srcJar);
            try {
                Files.copy(srcPath, this.srcJar);
            } catch (IOException e) {
                Log.error("Failed to copy srcJar", e);
            }
        }

        if (config.readInputDirectly) {
            if (this.mappingMode == MappingMode.JSON_COM) {
                this.patchFileDir = Paths.get(config.patchFileDir);
                Log.info("Set patchFileDir='{}'", this.patchFileDir);
            } else {
                Log.info("Using MappingFile='{}'", this.config.mappingFile);
            }
        } else {
            //Copy patchFile-Dir
            DirUtil.ensureCreatedAndEmpty(this.patchFileDir);

            Log.info("Copying patchFiles: '{}'->'{}'", config.patchFileDir, this.patchFileDir);
            Log.info("May take some time...");
            try {
                FileUtils.copyDirectory(Paths.get(config.patchFileDir).toFile(), this.patchFileDir.toFile());
            } catch (IOException e) {
                Log.error("Failed to copy patchFiles", e);
            }
        }

        Log.info("Preparing: Populating dirs Finished");
    }

    private void readPatchFiles() {
        Log.info("Starting reading patchFiles");
        var sw = StopWatch.createStarted();

        patchClassesSet = mappingMode == MappingMode.JSON_COM ?
                new ComJsonDirPatchFileReader().readPatchFiles(this.patchFileDir) :
                new NativeMappingsProcessor().readPatchFiles(this.config.mappingFile);

        sw.stop();
        Log.info("Finished reading patchFiles, took {}ms", sw.getTime());
    }


    private void unpackJAR() {
        Log.info("Starting unpacking jar");
        StopWatch sw = StopWatch.createStarted();

        var files = unpackJarInternal();

        if (config.inMemoryMode) {
            Log.info("Reading unpacked files");
            this.jarEntrys = files;
            this.classInfos =
                    files.stream()
                            .filter(f -> !f.IsDirectory && f.IsClass)
                            .map(f -> {
                                var ci = new ClassInfo();
                                ci.path = Paths.get(f.RelativeNamePath);
                                ci.data = f.Data;
                                return ci;
                            })
                            .collect(Collectors.toSet());
            Log.info("Reading unpacked files finished");
        } else {
            Log.info("Creating files and writing to disk... (may cause heavy load on disk)");
            DirUtil.ensureCreatedAndEmpty(this.classesDir);
            DirUtil.ensureCreatedAndEmpty(this.nonClassesDir);

            for (var file : files) {
                if (file.IsDirectory) {
                    file.File.mkdirs();
                    continue;
                }

                try {
                    FileUtils.writeByteArrayToFile(
                            file.File,
                            file.Data);
                } catch (IOException ex) {
                    Log.error("Failed to create class Files", ex);
                }
            }
        }

        sw.stop();
        Log.info("Finished unpacking jar, took {}ms", sw.getTime());
    }

    private Set<ExtractedJarFileEntryInfo> unpackJarInternal() {

        Set<ExtractedJarFileEntryInfo> files = new HashSet<>();

        Log.info("Unpacking");
        StopWatch sw = StopWatch.createStarted();
        long totalSizeProcessed = 0;

        try (var jar = new JarFile(this.srcJar.toFile())) {

            for (var entry : Collections.list(jar.entries())) {

                var extractedJarFileEntryInfo = new ExtractedJarFileEntryInfo();

                byte[] bytes = IOUtils.toByteArray(jar.getInputStream(entry));

                totalSizeProcessed += bytes.length;

                var validClass = entry.getName().endsWith(".class") && String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]).equalsIgnoreCase("cafebabe");

                extractedJarFileEntryInfo.RelativeNamePath = entry.getName();
                if (!config.inMemoryMode) {
                    var baseDir = validClass ? this.classesDir : this.nonClassesDir;
                    var file = baseDir
                            .resolve(entry.getName())
                            .toFile();
                    extractedJarFileEntryInfo.File = file;
                }
                extractedJarFileEntryInfo.IsClass = validClass;

                files.add(extractedJarFileEntryInfo);

                if (entry.isDirectory()) {
                    extractedJarFileEntryInfo.IsDirectory = true;
                    if (!config.inMemoryMode)
                        Log.debug("EXCT-MKDIR: '{}'", extractedJarFileEntryInfo.File);

                    continue;
                }

                if (!config.inMemoryMode)
                    Log.debug("EXCT-MKFILE: '{}' [Size={};VAL-CLASS={}]", extractedJarFileEntryInfo.File, bytes.length, validClass);

                extractedJarFileEntryInfo.Data = bytes;
            }
        } catch (IOException e) {
            throw new CancelExecutionException("Failed to read JAR", e);
        }

        sw.stop();
        Log.info("Unpacking finished, took {}ms", sw.getTime());

        Log.info("ClassFiles: {}x", files.stream().filter(f -> f.IsClass).count());
        Log.info("Processed: {} B", totalSizeProcessed);

        Log.info("Avg processing speed: ~{} B/s", Math.round(totalSizeProcessed / (Math.max(1, sw.getTime()) / 1000.0d)));

        return files;
    }

    private void preLoadClassesFromDisk() {
        Log.info("Starting preloading classes from disk");
        StopWatch sw = StopWatch.createStarted();

        Set<Path> paths;
        try (Stream<Path> pathStream = Files.walk(this.classesDir)) {
            paths = pathStream
                    .filter(p -> !Files.isDirectory(p))
                    .collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new CancelExecutionException("IO-Problem", ex);
        }

        Log.info("Preloading {}x classes from disk", paths.size());
        paths.forEach(path -> {
            var correctedPath = Paths.get(path.toString().substring(this.classesDir.toString().length() + 1));

            try {
                var ci = new ClassInfo();
                ci.path = correctedPath;
                ci.data = IOUtils.toByteArray(new FileInputStream(path.toFile()));
                classInfos.add(ci);
            } catch (IOException e) {
                Log.error("IO-Problem", e);
            }
        });


        sw.stop();
        Log.info("Finished preloading from disk, took {}ms", sw.getTime());
    }

    private void patchClassFiles() {
        Log.info("Start running Patcher");

        var patcherConfig = new PatcherConfig();

        patcherConfig.inMemory = this.config.inMemoryMode;
        patcherConfig.modifiedClassesDir = this.modifiedClassesDir;
        patcherConfig.classPatchFiles = this.patchClassesSet;
        patcherConfig.classInfos = this.classInfos;

        var patcher = new Patcher(patcherConfig);
        modifiedClasses = patcher.run();
    }

    private void packJar() {
        Log.info("Packing jar");

        var excludeComponents = this.config.excludeDoNotPackComponents
                .stream()
                .map(ExcludedComponent::new)
                .collect(Collectors.toList());

        try {
            if (config.inMemoryMode) {
                new InMemoryJarPacker(excludeComponents)
                        .packJarInMemory(
                                Stream.concat(
                                        this.jarEntrys.stream()
                                                .filter(entry -> !entry.IsClass)
                                                .map(entry -> (FileEntryInfo) entry),
                                        modifiedClasses.stream())
                                        .collect(Collectors.toSet()),
                                this.config.targetFile
                        );
            } else {
                new DiskJarPacker(excludeComponents)
                        .packJarFromDisk(
                                Arrays.asList(
                                        this.modifiedClassesDir,
                                        this.nonClassesDir),
                                this.config.targetFile
                        );
            }
        } catch (IOException e) {
            throw new CancelExecutionException("Failed to pack jar", e);
        }
    }

    enum MappingMode {
        /**
         * Mapping is done over a folder of json files
         */
        JSON_COM,
        /**
         * Mapping is done over a file
         */
        MAPPING_FILE
    }

}
