package javgent.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Utils for directories
 */
public class DirUtil {

    private DirUtil() {
        //NoImp pls
    }

    private static final Logger Log = LoggerFactory.getLogger(DirUtil.class);

    public static void ensureCreated(Path path) {
        File dir = path.toFile();
        //Not Exists
        if (!dir.isDirectory())
            DirUtil.createDir(dir);
    }

    public static void ensureCreatedAndEmpty(Path path) {
        File dir = path.toFile();
        //Exists
        if (dir.isDirectory())
            DirUtil.deleteDir(dir);

        DirUtil.createDir(dir);
    }

    public static void deleteDir(File dir) {
        Log.info("Deleting dir='{}'", dir.getPath());
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new DirUtilException("Failed to delete dir",e);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // Restore interrupted state...      
            Thread.currentThread().interrupt();
        }

        int timeout;
        for (timeout = 10; timeout > 0 && dir.isDirectory(); timeout--) {
            Log.debug("Dir still exists!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Restore interrupted state...      
                Thread.currentThread().interrupt();
            }
        }
        if (timeout <= 0) {
            throw new DirUtilException("Timed out!");
        }
    }

    public static void createDir(File dir) {
        Log.info("Creating dir='{}'", dir.getPath());
        dir.mkdirs();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // Restore interrupted state...      
            Thread.currentThread().interrupt();
        }

        int timeout;
        for (timeout = 10; timeout > 0 && !dir.isDirectory(); timeout--) {
            Log.debug("No dir");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Restore interrupted state...      
                Thread.currentThread().interrupt();
            }
        }
        if (timeout <= 0) {
            throw new DirUtilException("Timed out!");
        }
    }

    public static class DirUtilException extends RuntimeException {
        public DirUtilException() {
        }

        public DirUtilException(String message) {
            super(message);
        }

        public DirUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        public DirUtilException(Throwable cause) {
            super(cause);
        }

        public DirUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
