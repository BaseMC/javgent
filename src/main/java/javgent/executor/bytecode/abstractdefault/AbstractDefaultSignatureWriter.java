package javgent.executor.bytecode.abstractdefault;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.util.InnerClassTypeResolver;

public abstract class AbstractDefaultSignatureWriter extends AbstractClassBasedSignatureWriter {

    protected AbstractDefaultSignatureWriter(CurrentClassController controller) {
        super(controller);
    }

    @Override
    public void visitInnerClassType(String name) {
        var newName = InnerClassTypeResolver.resolve(controller, name);

        super.visitInnerClassType(newName);
    }

    @Override
    public void visitTypeVariable(String name) {
        var newName = controller.findNameByObfNameOrReturn(name);

        super.visitTypeVariable(newName);
    }
}
