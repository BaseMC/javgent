package javgent.executor.bytecode.clazz.visitor;

import javgent.executor.bytecode.abstractdefault.AbstractDescriptorWriter;
import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordDescriptorWriter extends AbstractDescriptorWriter {

    private static final Logger Log = LoggerFactory.getLogger(RecordDescriptorWriter.class);

    public RecordDescriptorWriter(CurrentClassController controller) {
        super(controller, Log);
    }

    public static String convert(CurrentClassController controller, String descriptor) {
        var writer = new RecordDescriptorWriter(controller);

        new SignatureReader(descriptor).accept(writer);

        return writer.toString();
    }
}
