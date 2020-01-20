package javgent.executor.execmodules;

import javgent.executor.bytecode.ClassControllerRegistry;
import javgent.executor.bytecode.clazz.BCClassVisitor;
import javgent.executor.bytecode.clazz.BCInitClassVisitor;
import javgent.util.DirUtil;
import javgent.util.ProgressManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * causes/instantiates bytecode modification
 */
public class ByteCodeModifier {

    private static final Logger Log = LoggerFactory.getLogger(ByteCodeModifier.class);

	private ByteCodeModifierConfig config;

	private ClassControllerRegistry registry;
	
	public ByteCodeModifier(ByteCodeModifierConfig config) {
		this.config = config;
	}
	
	public Set<FileEntryInfo> run() {
        var files = new HashSet<FileEntryInfo>();

	    if(!config.inMemory)
            DirUtil.ensureCreatedAndEmpty(config.modifiedClassesDir);

        registry = new ClassControllerRegistry(config.patchClassesMap);

        Log.info("Doing init");
        StopWatch swInit = StopWatch.createStarted();

        for (var classInfo : config.classSet) {
            init(classInfo.data);
        }
        swInit.stop();
        Log.info("Done init, took {}ms", swInit.getTime());

        var progress = new ProgressManager(Log, "Modifying {}x Classes", config.classSet.size(), "Modifying", 27);

		for (var classInfo : config.classSet) {

		    var transformedData = transform(classInfo.data);
		    var fei = new FileEntryInfo();
		    fei.RelativeNamePath = classInfo.path.toString();

		    if(!config.inMemory) {
                var modFile = config.modifiedClassesDir.resolve(classInfo.path).toFile();

                Log.debug("Modifying '{}'", modFile);

                try {
                    FileUtils.writeByteArrayToFile(
                            modFile,
                            transformedData);
                } catch (IOException ex) {
                    Log.error("IO-Problem", ex);
                }
            } else {
                fei.Data = transformedData;
            }

		    files.add(fei);

            progress.increment();
		}

		progress.progFinish();

        return files;
    }

    public byte[] transform(byte[] b) {
        final ClassReader classReader = new ClassReader(b);
        final ClassWriter cw = new ClassWriter(classReader,0);
        classReader.accept(new BCClassVisitor(cw, registry), ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    //Resolve things that are necessary, e.g. : SuperClass, Interfaces, etc
    public void init(byte[] b)
    {
        final ClassReader classReader = new ClassReader(b);
        classReader.accept(new BCInitClassVisitor( registry), ClassReader.EXPAND_FRAMES);
    }
}
