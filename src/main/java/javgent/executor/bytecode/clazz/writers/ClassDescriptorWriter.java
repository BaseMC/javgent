package javgent.executor.bytecode.clazz.writers;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassDescriptorWriter extends SignatureWriter {

    private static final Logger Log = LoggerFactory.getLogger(ClassDescriptorWriter.class);

    private CurrentClassController controller;

    public ClassDescriptorWriter(CurrentClassController controller) {
        this.controller = controller;
    }

    @Override
    public void visitClassType(String name) {
        var newName = controller.findNameByObfNameOrReturn(name);

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

    public static String convert(CurrentClassController currentClassController, String descriptor) {
        var writer = new ClassDescriptorWriter(currentClassController);

        new SignatureReader(descriptor).accept(writer);

        return writer.toString();
    }
}
