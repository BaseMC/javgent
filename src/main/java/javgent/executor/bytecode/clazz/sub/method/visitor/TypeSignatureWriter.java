package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeSignatureWriter extends SignatureWriter {

    private static final Logger Log = LoggerFactory.getLogger(TypeSignatureWriter.class);

    private CurrentClassController controller;

    public TypeSignatureWriter(CurrentClassController controller) {
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

        Log.warn("Visiting not implemented method! name='{}'", name);

        super.visitInnerClassType(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        String newName = controller.findNameByObfNameOrReturn(name);

        super.visitTypeVariable(newName);
    }

    public static String convert(CurrentClassController currentClassController, String signature) {
        if(signature == null)
            return null;

        // Non array has to be handled differently, else it will crash (IllegalArgumentException null)
        // Inputs can be e.g.
        // - ClassAObfuscatedAsABC <- Plain name
        // - [L...someSignatureStuff...; <- Signature
        // extremely illogical...
        if(!signature.startsWith("[L") || !signature.endsWith(";"))
            return currentClassController.findNameByObfNameOrReturn(signature);

        var writer = new TypeSignatureWriter(currentClassController);

        new SignatureReader(signature).accept(writer);

        return writer.toString();
    }
}
