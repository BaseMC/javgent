package javgent.executor.bytecode.clazz.writers;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.util.InnerClassTypeResolver;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;

public class ClassSignatureWriter extends SignatureWriter {
    
    private CurrentClassController controller;

    public ClassSignatureWriter(CurrentClassController controller) {
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

        var newName = InnerClassTypeResolver.resolve(controller, name);

        super.visitInnerClassType(newName);
    }

    @Override
    public void visitTypeVariable(String name) {
        String newName = controller.findNameByObfNameOrReturn(name);

        super.visitTypeVariable(newName);
    }

    public static String convert(CurrentClassController currentClassController, String signature) {
        var writer = new ClassSignatureWriter(currentClassController);

        new SignatureReader(signature).accept(writer);

        return writer.toString();
    }
}
