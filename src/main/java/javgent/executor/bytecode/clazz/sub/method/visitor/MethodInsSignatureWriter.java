package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.abstractdefault.AbstractClassBasedSignatureWriter;
import org.objectweb.asm.signature.SignatureReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInsSignatureWriter extends AbstractClassBasedSignatureWriter {

    private static final Logger LOG = LoggerFactory.getLogger(MethodInsSignatureWriter.class);

    public MethodInsSignatureWriter(CurrentClassController controller) {
        super(controller);
    }

    @Override
    public void visitInnerClassType(String name) {
        LOG.warn("Visiting not implemented method! name='{}'", name);

        super.visitInnerClassType(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        LOG.warn("Visiting not implemented method! name='{}'", name);

        super.visitTypeVariable(name);
    }

    public static String convert(CurrentClassController currentClassController, String signature) {
        var writer = new MethodInsSignatureWriter(currentClassController);

        new SignatureReader(signature).accept(writer);

        return writer.toString();
    }
}
