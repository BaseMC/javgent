package javgent.util;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;

import java.text.DecimalFormat;

/**
 * Gives feedback (by the logger), about the progress of something
 */
public class ProgressManager {

    private final Logger logger;
    private final String progressMessage;
    private final long count;
    private final StopWatch sw;

    private double divider;
    private long counter = 1;


    public ProgressManager(Logger logger, long count, String progressMessage) {
        this(logger, null, count, progressMessage);
    }

    public ProgressManager(Logger logger, String initMsg, long count, String progressMessage) {
        this(logger, initMsg, count, progressMessage,17);
    }

    public ProgressManager(Logger logger, String initMsg, long count, String progressMessage, double dividerFactor) {
        this.logger = logger;
        this.count = count;
        this.progressMessage = progressMessage;

        this.divider = Math.floor(count / dividerFactor);

       progInit(initMsg);

        sw = StopWatch.createStarted();
    }

    private void progInit(String initMsg) {
        if(initMsg != null)
            logger.info(initMsg, count);
    }

    public void increment() {
        progIncWrite();

        counter++;
    }

    private void progIncWrite() {
        if (counter % divider != 0)
            return;

        var percent = counter * 100 / (double) count;

        if(percent == 100)
            return;

        var percentageStr = new DecimalFormat("0.##").format(percent);
        logger.info("{}: {}% [{}/{}]",
                progressMessage,
                percentageStr,
                counter,
                count);
    }

    public void progFinish() {
        sw.stop();
        logger.info("{}: 100% (took {}ms)", progressMessage, sw.getTime());
    }

    public Logger getLogger() {
        return logger;
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public long getCount() {
        return count;
    }

    public double getDivider() {
        return divider;
    }

    public void setDivider(double divider) {
        this.divider = divider;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}
