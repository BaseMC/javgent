package javgent.executor.bytecode.clazz;

import javgent.executor.bytecode.clazz.writers.ClassSignatureWriter;

import java.util.Arrays;

public class ModifierForClass {
    private CurrentClassController controller;

    public ModifierForClass(CurrentClassController controller) {
        this.controller = controller;
    }

    public String name(String name) {
        if (name == null)
            throw new RuntimeException("Something weird happened: Name of class was null!");

        return controller.getCurrentPatchClass().Name;
    }

    public String signature(String signature) {
        if (signature == null)
            return null;

        return ClassSignatureWriter.convert(controller,signature);
    }

    public String superName(String superName) {
        if (superName == null)
            return null;

        var optSuperController = controller.findPatchControllerByObfName(superName);
        if(optSuperController.isEmpty())
            return superName;

        var superController = optSuperController.get();

        return superController.getCurrentPatchClass().Name;
    }

    public String[] interfaces(String[] interfaces) {
        return Arrays.stream(interfaces)
                .map(interf -> {

                    var optInterfController = controller.findPatchControllerByObfName(interf);
                    if(optInterfController.isEmpty())
                        return interf;

                    var interfController = optInterfController.get();

                    return interfController.getCurrentPatchClass().Name;
                })
                .toArray(String[]::new);
    }
}
