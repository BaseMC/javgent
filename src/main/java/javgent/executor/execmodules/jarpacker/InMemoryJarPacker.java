package javgent.executor.execmodules.jarpacker;

import javgent.executor.execmodules.ExcludedComponent;
import javgent.executor.execmodules.FileEntryInfo;
import javgent.util.ProgressManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

public class InMemoryJarPacker extends JarPacker {

    private static final Logger Log = LoggerFactory.getLogger(InMemoryJarPacker.class);

    public InMemoryJarPacker(List<ExcludedComponent> excludedComponents) {
        super(excludedComponents);
    }

    public void packJarInMemory(Collection<FileEntryInfo> fileEntryInfos, String outputPath) throws IOException {

        var progress = new ProgressManager(Log, "Packing {}x FileEntryInfos", fileEntryInfos.size(), "Packing", 7);

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(outputPath), manifest)) {
            for (var fei : fileEntryInfos) {

                addInMemory(fei, target);

                progress.increment();
            }
        }

        progress.progFinish();
    }

    protected String buildRelativeName(FileEntryInfo fei) {
        var relativeName = fei.RelativeNamePath.replace("\\", "/");

        relativeName = replaceMetaInf(relativeName);

        if (fei.IsDirectory && !fei.RelativeNamePath.endsWith("/"))
            relativeName += "/";

        return relativeName;
    }

    private void addInMemory(FileEntryInfo fei, JarOutputStream target) throws IOException {

        var relativeName = buildRelativeName(fei);
        if(this.excludedComponents
                .parallelStream()
                .anyMatch(exc -> relativeName.startsWith(exc.getRelativeNamePath()))) {
            Log.debug("Excluded: {}", relativeName);
            return;
        }

        JarEntry entry = new JarEntry(relativeName);
        try {
            target.putNextEntry(entry);
            if (!fei.IsDirectory)
                target.write(fei.Data);
        } catch (ZipException ex) {
            //Dirs are sometimes duplicated and through that are just logged as Debug
            if (fei.IsDirectory)
                Log.debug(
                        String.format(
                                "Failure whilst adding dir '%s'",
                                relativeName
                        ),
                        ex);
            else
                Log.warn(
                        String.format(
                                "Failure whilst adding file '%s'",
                                relativeName
                        ),
                        ex);
        }
        target.closeEntry();

    }
}
