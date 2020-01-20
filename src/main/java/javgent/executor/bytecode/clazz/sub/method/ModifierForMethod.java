package javgent.executor.bytecode.clazz.sub.method;

import javgent.executor.bytecode.clazz.sub.method.controller.MethodSelector;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodDescriptorWriter;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodSelectorVistiorDetector;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodSignatureWriter;
import javgent.executor.model.PatchMethod;

import java.util.Arrays;

public class ModifierForMethod {
    private CurrentMethodController controller;
    private PatchMethod currentPatchMethod;

    public ModifierForMethod(CurrentMethodController controller) {
        this.controller = controller;
    }

    public boolean tryInit(String obfName, String descriptor) {
        var selector = findSelector(descriptor, null);

        var optComPatchMethod = controller.findByObfNameAndSelector(obfName, selector);
        if (optComPatchMethod.isPresent())
            currentPatchMethod = optComPatchMethod.get();

        return optComPatchMethod.isPresent();
    }

    private MethodSelector findSelector(String desc, String signature) {

        return MethodSelectorVistiorDetector.findSelector(controller.getCurrentClassController(), desc, signature);
    }

    public String name(String name) {
        if (name == null)
            throw new RuntimeException("Something weird happened: Name of method was null!");

        return currentPatchMethod.Name;
    }

    public String descriptor(String descriptor) {
        if (descriptor == null)
            return null;

        return MethodDescriptorWriter.convert(controller.getCurrentClassController(),descriptor);
    }


    public String signature(String signature) {
        if (signature == null)
            return null;

        return MethodSignatureWriter.convert(controller.getCurrentClassController(),signature);
    }

    @SuppressWarnings("squid:S1168")
    public String[] exceptions(String[] exceptions) {
        if (exceptions == null)
            return null;

        return Arrays.stream(exceptions)
                .map(interf ->
                        controller.getCurrentClassController().findNameByObfNameOrReturn(interf))
                .toArray(String[]::new);
    }

    public CurrentMethodController getController() {
        return controller;
    }

    public PatchMethod getCurrentPatchMethod() {
        return currentPatchMethod;
    }
}
