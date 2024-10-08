package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.abstractdefault.AbstractDefaultSignatureWriter;
import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;

public class MethodSignatureWriter extends AbstractDefaultSignatureWriter {

    public MethodSignatureWriter(CurrentClassController controller) {
        super(controller);
    }

    public static String convert(CurrentClassController currentClassController, String signature) {
        if(signature == null)
            return null;

        var writer = new MethodSignatureWriter(currentClassController);

        new SignatureReader(signature).accept(writer);

        return writer.toString();
    }
}
