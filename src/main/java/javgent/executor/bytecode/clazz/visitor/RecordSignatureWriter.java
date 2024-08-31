package javgent.executor.bytecode.clazz.visitor;

import javgent.executor.bytecode.abstractdefault.AbstractDefaultSignatureWriter;
import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;

public class RecordSignatureWriter extends AbstractDefaultSignatureWriter {

    public RecordSignatureWriter(CurrentClassController controller) {
        super(controller);
    }

    public static String convert(CurrentClassController currentClassController, String signature) {
        if(signature == null)
            return null;

        var writer = new RecordSignatureWriter(currentClassController);

        new SignatureReader(signature).accept(writer);

        return writer.toString();
    }
}
