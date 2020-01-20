package javgent.executor.execmodules.jarpacker;

import javgent.executor.execmodules.ExcludedComponent;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

/**
 * JarPacker for diskmode={@code true}
 */
public class DiskJarPacker extends JarPacker {

    private static final Logger Log = LoggerFactory.getLogger(DiskJarPacker.class);

    public DiskJarPacker(List<ExcludedComponent> excludedComponents) {
        super(excludedComponents);
    }

    public void packJarFromDisk(Collection<Path> exportDirs, String outputPath) throws IOException {

        var sw = StopWatch.createStarted();
        Log.info("Starting packing jar");

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(outputPath), manifest)) {
            for (var exportDir : exportDirs) {
                addFromDisk(exportDir, exportDir.toFile(), target);
            }
        }

        sw.stop();
        Log.info("Finished packing jar, took {}ms", sw.getTime());
    }

    private void addFromDisk(Path baseDir, File source, JarOutputStream target) throws IOException {

        if (source.isDirectory()) {

            if(addFolderFromDisk(baseDir, source, target))
                for (File nestedFile : source.listFiles())
                    addFromDisk(baseDir, nestedFile, target);

            return;
        }

        addFileFromDisk(baseDir, source, target);
    }

    protected String buildEntryFolderName(Path baseDir, File source) {
        var entryFolderName = source.getPath().substring(baseDir.toString().length() + 1).replace("\\", "/");

       entryFolderName = replaceMetaInf(entryFolderName);

        if (entryFolderName.isEmpty())
            return null;

        if (!entryFolderName.endsWith("/"))
            entryFolderName += "/";

        return entryFolderName;
    }

    protected String buildEntryFileName(Path baseDir, File source) {
        var entryFileName = source.getPath().substring(baseDir.toString().length() + 1).replace("\\", "/");

        entryFileName = replaceMetaInf(entryFileName);

        return entryFileName;
    }

    /**
     *
     * @param baseDir
     * @param source
     * @param target
     * @return true = not excluded
     * @throws IOException
     */
    private boolean addFolderFromDisk(Path baseDir, File source, JarOutputStream target) throws IOException {
        //Check if current dir is baseDir
        if (baseDir.toFile().equals(source))
            return true;

        String entryFolderName = buildEntryFolderName(baseDir,source);
        if(entryFolderName == null)
            return true;

        if(this.excludedComponents.parallelStream()
                .filter(ExcludedComponent::isDirectory)
                .anyMatch(exc -> entryFolderName.startsWith(exc.getRelativeNamePath()))) {
            Log.debug("Excluded: {}", entryFolderName);
            return false;
        }

        JarEntry entry = new JarEntry(entryFolderName);
        entry.setTime(source.lastModified());

        try {
            target.putNextEntry(entry);
        } catch (ZipException ex) {
            Log.debug(
                    String.format(
                            "Failure whilst adding dir '%s'",
                            entryFolderName
                    ),
                    ex);
        }

        target.closeEntry();

        return true;
    }

    private void addFileFromDisk(Path baseDir, File source, JarOutputStream target) throws IOException {
        var entryName = buildEntryFileName(baseDir, source);

        if(this.excludedComponents.parallelStream()
                .filter(exc -> !exc.isDirectory())
                .anyMatch(exc -> entryName.startsWith(exc.getRelativeNamePath()))) {
            Log.debug("Excluded: {}", entryName);
            return;
        }

        JarEntry entry = new JarEntry(entryName);
        entry.setTime(source.lastModified());
        try {
            target.putNextEntry(entry);

            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {

                byte[] buffer = new byte[1024];
                while (true) {
                    int count = in.read(buffer);
                    if (count == -1)
                        break;
                    target.write(buffer, 0, count);
                }
            }
        } catch (ZipException ex) {
            Log.warn(
                    String.format(
                            "Failure whilst adding file '%s'",
                            entryName
                    ),
                    ex);
        }
        target.closeEntry();
    }
}
