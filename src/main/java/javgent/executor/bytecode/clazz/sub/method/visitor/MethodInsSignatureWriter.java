package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInsSignatureWriter extends SignatureWriter {

    private static final Logger Log = LoggerFactory.getLogger(MethodInsSignatureWriter.class);

    private CurrentClassController controller;

    public MethodInsSignatureWriter(CurrentClassController controller) {
        this.controller = controller;
    }

    @Override
    public void visitClassType(String name) {

        String newName = controller.findNameByObfNameOrReturn(name);

        super.visitClassType(newName);
    }

    @Override
    public void visitFormalTypeParameter(String name) {

        String newName = controller.findNameByObfNameOrReturn(name);

        super.visitFormalTypeParameter(newName);
    }

    @Override
    public void visitInnerClassType(String name) {
        if(name.length() > 1)
            Log.warn("Visiting not implemented method! name='{}'", name);

        super.visitInnerClassType(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        if(name.length() > 1)
            Log.warn("Visiting not implemented method! name='{}'", name);

        super.visitTypeVariable(name);
    }

    public static String convert(CurrentClassController currentClassController, String signature) {
        var writer = new MethodInsSignatureWriter(currentClassController);

        new SignatureReader(signature).accept(writer);

        return writer.toString();
    }
}
