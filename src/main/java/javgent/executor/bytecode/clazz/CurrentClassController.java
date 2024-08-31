package javgent.executor.bytecode.clazz;

import javgent.executor.bytecode.ClassControllerRegistry;
import javgent.executor.bytecode.clazz.sub.field.CurrentFieldsController;
import javgent.executor.bytecode.clazz.sub.method.CurrentMethodController;
import javgent.executor.model.PatchClass;

import java.util.Optional;

public class CurrentClassController {

    private ClassControllerRegistry registry;
    private PatchClass currentPatchClass;

    private CurrentFieldsController fieldsController;
    private CurrentMethodController methodController;

    public CurrentClassController(ClassControllerRegistry registry, PatchClass currentPatchClass) {
        this.registry = registry;
        this.currentPatchClass = currentPatchClass;
    }

    public PatchClass getCurrentPatchClass() {
        return currentPatchClass;
    }

    public String findNameByObfNameOrReturn(String obfName) {
        var opt = findPatchClassByObfName(obfName);
        if (opt.isEmpty())
            return obfName;
        return opt.get().Name;
    }

    public Optional<PatchClass> findPatchClassByObfName(String obfName) {

        if(obfName.equals(this.currentPatchClass.ObfName))
            return Optional.of(currentPatchClass);

        var optController = findPatchControllerByObfName(obfName);
        if(optController.isEmpty())
            return Optional.empty();

        var controller = optController.get();
        return Optional.ofNullable(controller.currentPatchClass);
    }

    public Optional<CurrentClassController> findPatchControllerByObfName(String obfName) {
        return registry.getByObfName(obfName);
    }

    public ClassControllerRegistry getRegistry() {
        return registry;
    }

    public CurrentFieldsController getFieldsController() {
        if(this.fieldsController == null)
            this.fieldsController = new CurrentFieldsController(this);

        return this.fieldsController;
    }

    public CurrentMethodController getMethodController() {
        if(this.methodController == null)
            this.methodController = new CurrentMethodController(this);

        return this.methodController;
    }

}
