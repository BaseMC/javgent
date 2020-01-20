package javgent.executor.bytecode.clazz.sub.field;

import javgent.executor.bytecode.clazz.writers.ClassDescriptorWriter;
import javgent.executor.bytecode.clazz.writers.ClassSignatureWriter;

public class ModifierForField {
    private CurrentFieldsController controller;

    public ModifierForField(CurrentFieldsController controller) {
        this.controller = controller;
    }

    public String name(String name) {
        return controller.findNameByObfNameOrReturn(name);
    }

    public String descriptor(String descriptor) {
        if (descriptor == null)
            return null;

        return ClassDescriptorWriter.convert(controller.getCurrentClassController(),descriptor);
    }

    public String signature(String signature) {
        if(signature == null)
            return  null;

        return ClassSignatureWriter.convert(controller.getCurrentClassController(),signature);
    }

}
