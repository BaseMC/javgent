package javgent.executor.bytecode.abstractdefault;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureWriter;

public abstract class AbstractClassBasedSignatureWriter extends SignatureWriter {

    protected CurrentClassController controller;

    protected AbstractClassBasedSignatureWriter(CurrentClassController controller) {
        this.controller = controller;
    }

    @Override
    public void visitClassType(String name) {
        var newName = controller.findNameByObfNameOrReturn(name);

        super.visitClassType(newName);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        var newName = controller.findNameByObfNameOrReturn(name);

        super.visitFormalTypeParameter(newName);
    }
}
