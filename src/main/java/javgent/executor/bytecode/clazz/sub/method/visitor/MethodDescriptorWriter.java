package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodDescriptorWriter extends SignatureWriter {

    private static final Logger Log = LoggerFactory.getLogger(MethodDescriptorWriter.class);

    private CurrentClassController controller;

    public MethodDescriptorWriter(CurrentClassController controller) {
        this.controller = controller;
    }

    @Override
    public void visitClassType(String name) {

        String newName = controller.findNameByObfNameOrReturn(name);

        super.visitClassType(newName);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        Log.warn("Visiting not implemented method 'visitFormalTypeParameter'! name='{}'", name);
        super.visitFormalTypeParameter(name);
    }

    @Override
    public void visitInnerClassType(String name) {
        Log.warn("Visiting not implemented method 'visitInnerClassType'! name='{}'", name);
        super.visitInnerClassType(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        Log.warn("Visiting not implemented method 'visitTypeVariable'! name='{}'", name);
        super.visitTypeVariable(name);
    }

    public static String convert(CurrentClassController controller, String descriptor) {
        var writer = new MethodDescriptorWriter(controller);

        new SignatureReader(descriptor).accept(writer);

        return writer.toString();
    }
}
