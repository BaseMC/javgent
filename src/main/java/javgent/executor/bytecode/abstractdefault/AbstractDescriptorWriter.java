package javgent.executor.bytecode.abstractdefault;

import javgent.executor.bytecode.clazz.CurrentClassController;
import org.objectweb.asm.signature.SignatureWriter;
import org.slf4j.Logger;

public abstract class AbstractDescriptorWriter extends SignatureWriter {

    protected CurrentClassController controller;
    protected Logger log;

    protected AbstractDescriptorWriter(CurrentClassController controller, Logger log) {
        this.controller = controller;
        this.log = log;
    }

    @Override
    public void visitClassType(String name) {
        var newName = controller.findNameByObfNameOrReturn(name);

        super.visitClassType(newName);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        log.warn("Visiting not implemented method 'visitFormalTypeParameter'! name='{}'", name);
        super.visitFormalTypeParameter(name);
    }

    @Override
    public void visitInnerClassType(String name) {
        log.warn("Visiting not implemented method 'visitInnerClassType'! name='{}'", name);
        super.visitInnerClassType(name);
    }

    @Override
    public void visitTypeVariable(String name) {
        log.warn("Visiting not implemented method 'visitTypeVariable'! name='{}'", name);
        super.visitTypeVariable(name);
    }
}
