package javgent.executor.execmodules.patchfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import javgent.executor.CancelExecutionException;
import javgent.executor.model.PatchClass;
import javgent.executor.modelconverter.ComPatchConverter;
import javgent.util.ProgressManager;
import javgent.commodel.ComPatchClass;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads mappings from a directory with .json files
 */
public class ComJsonDirPatchFileReader {

    private static final Logger Log = LoggerFactory.getLogger(ComJsonDirPatchFileReader.class);

    public Set<PatchClass> readPatchFiles(Path patchFileDir) {

        Log.debug("Creating JSON mapper");
        ObjectMapper objMapper = new ObjectMapper();

        Log.info("Start reading patchFile-locations");
        StopWatch sw = StopWatch.createStarted();

        Set<Path> patchFilePaths;
        try (Stream<Path> paths = Files.walk(patchFileDir)) {
            patchFilePaths = paths
                    .filter(path -> !Files.isDirectory(path))
                    .collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new CancelExecutionException("IO-Problem while reading patchFile-locations", ex);
        }
        sw.stop();
        Log.info("Finished reading patchFile-locations, took {}ms", sw.getTime());


        var progress = new ProgressManager(Log, "Will read {}x patchFiles", patchFilePaths.size(), "Reading patchFiles");

        Set<PatchClass> patchClassesSet = new HashSet<>();
        for (var path : patchFilePaths) {

            var patchFile = readPatchFile(objMapper, path);

            patchClassesSet.add(patchFile);

            progress.increment();
        }

        progress.progFinish();

        return patchClassesSet;
    }

    private PatchClass readPatchFile(ObjectMapper objMapper,Path patchFilePath) {
        Log.debug("Reading PatchClass: '{}'", patchFilePath);

        try {
            var comPatchClass = objMapper.readValue(patchFilePath.toFile(), ComPatchClass.class);
            return ComPatchConverter.convert(comPatchClass);
        } catch (Exception e) {
            Log.error("Failed to read ComPatchClass", e);
            return null;
        }
    }
}
