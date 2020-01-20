package javgent;

import javgent.executor.execmodules.ExecutorConfig;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Commandlineparser
 */
//squid:S106 Standard outputs should not be used directly to log anything - It's used on the console so...
@SuppressWarnings("squid:S106")
public class Parser {

    private Parser() {
        //NoImpl pls
    }

    public static final String SRC_FILE = "srcfile";
    public static final String PATCH_FILE_DIR = "patchfiledir";
    public static final String OUTPUT_FILE = "outputfile";

    public static final String MAPPING_FILE = "mapping";

    public static final String EXCLUDE_COMPONENTS = "excludecomp";

    public static final String ON_DISK = "diskmode";

    public static final String WORK_DIR = "workdir";
    public static final String READ_INPUT_DIRECTLY = "readinputdirectly";
    public static final String CLEAN_WORK_DIR = "cleanworkdir";

    public static Optional<ExecutorConfig> parse(String[] args) {
        Options options = new Options();

        Option srcFile =
                new Option("s", SRC_FILE, true, "Java Source File (.jar)");
        srcFile.setRequired(true);
        options.addOption(srcFile);

        Option patchFileDir =
                new Option("p", PATCH_FILE_DIR, true, "patchFile-Directory  [Mutually exclusive to " + MAPPING_FILE + "; but one is required]");
        options.addOption(patchFileDir);

        Option mappingFile =
                new Option("m", MAPPING_FILE, true, "MappingFile [Mutually exclusive to " + PATCH_FILE_DIR + "; but one is required]");
        options.addOption(mappingFile);

        Option output =
                new Option("o", OUTPUT_FILE, true, "outputFile");
        output.setRequired(true);
        options.addOption(output);

        Option excludeComponents =
                new Option("ec", EXCLUDE_COMPONENTS, true, "Packages/Directory-Structures (with /) or files that should be excluded; separated by ,; useful for compiled third party libs");
        excludeComponents.setValueSeparator(',');
        excludeComponents.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(excludeComponents);

        Option onDisk =
                new Option("d", ON_DISK, false, "Write files to disk. If not set (default) patches occur in Memory");
        options.addOption(onDisk);

        Option workDir =
                new Option("dw", WORK_DIR, true, "[DISKMODE] working directory");
        options.addOption(workDir);

        Option readInputDirectly =
                new Option("dr", READ_INPUT_DIRECTLY, false, "[DISKMODE] read input directly (don't copy srcFile and patchFiles)");
        options.addOption(readInputDirectly);

        Option cleanWorkDir =
                new Option("dc", CLEAN_WORK_DIR, false, "[DISKMODE] clean working directory");
        options.addOption(cleanWorkDir);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);

            if(cmd.hasOption(PATCH_FILE_DIR) && cmd.hasOption(MAPPING_FILE))
                throw new ParseException(PATCH_FILE_DIR + " and " + MAPPING_FILE + " are mutually exclusive");
            else if(!cmd.hasOption(PATCH_FILE_DIR) && !cmd.hasOption(MAPPING_FILE))
                throw new ParseException(PATCH_FILE_DIR + " or " + MAPPING_FILE + " is required");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("javgent", options);

            System.exit(1);
            return Optional.empty();
        }

        var config = new ExecutorConfig();


        config.inMemoryMode = !cmd.hasOption(ON_DISK);
        if (!config.inMemoryMode) {
            doOnDiskConfig(config, cmd);
        }

        config.targetFile = cmd.getOptionValue(OUTPUT_FILE);
        config.srcFile = cmd.getOptionValue(SRC_FILE);
        if(cmd.hasOption(PATCH_FILE_DIR))
            config.patchFileDir = cmd.getOptionValue(PATCH_FILE_DIR);
        else if(cmd.hasOption(MAPPING_FILE))
            config.mappingFile = cmd.getOptionValue(MAPPING_FILE);

        config.excludeDoNotPackComponents =
                new ArrayList<>(Arrays.asList(
                        cmd.getOptionValues(EXCLUDE_COMPONENTS)));


        return Optional.ofNullable(config);
    }

    private static void doOnDiskConfig(ExecutorConfig config, CommandLine cmd) {
        if (cmd.hasOption(WORK_DIR))
            config.workDir = cmd.getOptionValue(WORK_DIR);

        if(cmd.hasOption(READ_INPUT_DIRECTLY))
            config.readInputDirectly = true;

        if(cmd.hasOption(CLEAN_WORK_DIR))
            config.cleanWorkDir = true;
    }
}
