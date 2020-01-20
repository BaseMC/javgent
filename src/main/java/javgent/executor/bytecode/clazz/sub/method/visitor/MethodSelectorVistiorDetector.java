package javgent.executor.bytecode.clazz.sub.method.visitor;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.sub.method.controller.MethodSelector;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;

public class MethodSelectorVistiorDetector extends SignatureWriter {

    private CurrentClassController controller;

    private MethodSelector selector = new MethodSelector();

    private boolean inParameters = false;
    private boolean isReturn = false;
    private int arrayCounter = 0;
    private int genericInsideCount = 0;

    public MethodSelectorVistiorDetector(CurrentClassController controller) {
        this.controller = controller;
    }

    private String postProcessName(String name) {
        if(arrayCounter > 0)
        {
            name = name + "[]".repeat(arrayCounter);
            arrayCounter = 0;
        }
        return name;
    }

    private void add(String name) {

        var newName = postProcessName(name);

        if (isReturn) {
            selector.setReturnType(newName);
            isReturn = false;
        } else if (inParameters) {
            selector.addParameterType(newName);
        }
    }

    @Override
    public void visitClassType(String name) {

        if(genericInsideCount > 0) {
            super.visitClassType(name);
            return;
        }

        var unObfName = controller.findNameByObfNameOrReturn(name);

        add(unObfName);

        super.visitClassType(name);
    }

    @Override
    public void visitBaseType(char descriptor) {

        if(genericInsideCount > 0) {
            super.visitBaseType(descriptor);
            return;
        }

        var translated = Type.getType(String.valueOf(descriptor)).getClassName();
        add(translated);

        super.visitBaseType(descriptor);
    }

    @Override
    public SignatureVisitor visitParameterType() {
        if(genericInsideCount > 0) {
            return super.visitParameterType();
        }

        inParameters = true;
        isReturn = false;
        return super.visitParameterType();
    }

    @Override
    public SignatureVisitor visitReturnType() {

        if(genericInsideCount > 0) {
            return super.visitReturnType();
        }

        inParameters = false;
        isReturn = true;
        return super.visitReturnType();
    }

    @Override
    public SignatureVisitor visitArrayType() {
        arrayCounter++;
        return super.visitArrayType();
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        genericInsideCount++;
        return super.visitTypeArgument(wildcard);
    }

    @Override
    public void visitEnd() {
        if(genericInsideCount > 0)
            genericInsideCount--;
        super.visitEnd();
    }

    public MethodSelector getSelector() {
        return this.selector;
    }

    public static MethodSelector findSelector(CurrentClassController controller, String desc, String signature) {

        var input = signature == null ? desc : signature;

        var writer = new MethodSelectorVistiorDetector(controller);

        new SignatureReader(input).accept(writer);

        return writer.getSelector();
    }
}
