package javgent.executor.bytecode.abstractdefault;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureWriter;

public class AbstractClassBasedSignatureWriter extends SignatureWriter {

    protected CurrentClassController controller;

    public AbstractClassBasedSignatureWriter(CurrentClassController controller) {
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
