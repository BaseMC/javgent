package javgent.executor.bytecode.clazz.sub.field;

import javgent.ASMAC;
import javgent.executor.bytecode.clazz.writers.ClassDescriptorWriter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCFieldVisitor extends FieldVisitor {

    private static final Logger Log = LoggerFactory.getLogger(BCFieldVisitor.class);

    private CurrentFieldsController controller;

    public BCFieldVisitor(CurrentFieldsController controller, FieldVisitor fieldVisitor) {
        super(ASMAC.ASM_VERSION, fieldVisitor);
        this.controller = controller;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

        String newDescriptor = ClassDescriptorWriter.convert(controller.getCurrentClassController(), descriptor);

        return super.visitAnnotation(newDescriptor, visible);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        Log.warn("Visiting not implemented 'visitAttribute'! type='{}'", attribute.type);
        super.visitAttribute(attribute);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        Log.warn("Visiting not implemented 'visitTypeAnnotation'! typePath='{}',descriptor='{}'", typePath, descriptor);
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }
}
