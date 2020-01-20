package javgent;

import javgent.executor.execmodules.Executor;
import javgent.executor.execmodules.ExecutorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Main class
 */
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOG.error("Uncaught exception", e);
            System.exit(-1);
        });

        LOG.info("project and license information: https://github.com/BaseMC/javgent");

        try {
            Optional<ExecutorConfig> optionalExecutorConfig = Parser.parse(args);
            if (optionalExecutorConfig.isEmpty())
                return;

            new Executor(optionalExecutorConfig.get()).run();

        } catch (Exception ex) {
            LOG.error("Exception in main", ex);
            System.exit(-1);
        }
    }
}
