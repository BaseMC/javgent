package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.abstractdefault.AbstractDescriptorWriter;
import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodDescriptorWriter extends AbstractDescriptorWriter {

    private static final Logger Log = LoggerFactory.getLogger(MethodDescriptorWriter.class);

    public MethodDescriptorWriter(CurrentClassController controller) {
        super(controller, Log);
    }

    public static String convert(CurrentClassController controller, String descriptor) {
        var writer = new MethodDescriptorWriter(controller);

        new SignatureReader(descriptor).accept(writer);

        return writer.toString();
    }
}
