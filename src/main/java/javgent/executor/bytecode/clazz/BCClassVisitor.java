package javgent.executor.bytecode.clazz;

import javgent.ASMAC;
import javgent.executor.bytecode.ClassControllerRegistry;
import javgent.executor.bytecode.clazz.sub.field.BCFieldVisitor;
import javgent.executor.bytecode.clazz.sub.field.ModifierForField;
import javgent.executor.bytecode.clazz.sub.method.BCMethodVisitor;
import javgent.executor.bytecode.clazz.sub.method.ModifierForMethod;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodSignatureWriter;
import javgent.executor.bytecode.clazz.util.RemoteMethodResolver;
import javgent.executor.bytecode.clazz.writers.ClassDescriptorWriter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class BCClassVisitor extends ClassVisitor {

    private static final Logger Log = LoggerFactory.getLogger(BCClassVisitor.class);

    private ClassControllerRegistry registry;
    private CurrentClassController controller;


    public BCClassVisitor(ClassVisitor cv, ClassControllerRegistry registry) {
        super(ASMAC.ASM_VERSION, cv);
        this.registry = registry;
    }


    private void getAndInstallController(String obfName) {

        Optional<CurrentClassController> optionalCurrentClassController =
                registry.getByObfName(obfName);

        if (optionalCurrentClassController.isEmpty()) {
            return;
        }

        controller = optionalCurrentClassController.get();
    }

    private boolean controllerPresent() {
        return this.controller != null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        getAndInstallController(name);

        if (controllerPresent()) {

            //Modify name, signature, superName, interfaces
            var modifier = new ModifierForClass(controller);
            name = modifier.name(name);
            signature = modifier.signature(signature);
            superName = modifier.superName(superName);
            interfaces = modifier.interfaces(interfaces);
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {

        if (!controllerPresent())
            return super.visitField(access, name, descriptor, signature, value);


        //Modify name, descriptor, signature
        var modifier = new ModifierForField(controller.getFieldsController());
        name = modifier.name(name);
        descriptor = modifier.descriptor(descriptor);
        signature = modifier.signature(signature);

        return new BCFieldVisitor(controller.getFieldsController(), super.visitField(access, name, descriptor, signature, value));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        if (!controllerPresent())
            return super.visitMethod(access, name, descriptor, signature, exceptions);


        var modifier = new ModifierForMethod(controller.getMethodController());
        if (!modifier.tryInit(name, descriptor))
            //Init failed
            return super.visitMethod(access, name, descriptor, signature, exceptions);

        //Modify name, descriptor, signature, exceptions
        name = modifier.name(name);
        descriptor = modifier.descriptor(descriptor);
        signature = modifier.signature(signature);
        exceptions = modifier.exceptions(exceptions);

        return new BCMethodVisitor(modifier, super.visitMethod(access, name, descriptor, signature, exceptions));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

        if (!controllerPresent())
            return super.visitAnnotation(descriptor, visible);

        //Modify descriptor
        descriptor = ClassDescriptorWriter.convert(controller, descriptor);

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {

        if (!controllerPresent()) {
            super.visitOuterClass(owner, name, descriptor);
            return;
        }

        //name of a method (likely)
        var optNewController = controller.getRegistry().getByObfName(owner);
        if (!optNewController.isPresent()) {
            Log.debug("Unable to get controller for owner='{}'", owner);
            super.visitOuterClass(owner, name, descriptor);
            return;
        }
        var newController = optNewController.get();

        var optResult = RemoteMethodResolver.resolve(
                newController,
                owner,
                name,
                descriptor);

        if (!optResult.isPresent()) {
            Log.debug("Unable to get result for owner='{}', name='{}'", owner, name);
            super.visitOuterClass(owner, name, descriptor);
            return;
        }

        var result = optResult.get();

        var optName = result.Name;
        if (optName.isPresent())
            name = optName.get();

        if (descriptor != null)
            descriptor = MethodSignatureWriter.convert(controller, descriptor);

        super.visitOuterClass(result.Owner, name, descriptor);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {

        if (!controllerPresent()) {
            super.visitInnerClass(name, outerName, innerName, access);
            return;
        }

        //Modify name, outerName, innerName
        String newName = controller.findNameByObfNameOrReturn(name);

        if (name.equals(newName)) {
            super.visitInnerClass(name, outerName, innerName, access);
            return;
        }

        var parts = newName.split("\\$");
        outerName = parts[0];
        innerName = parts[1];

        super.visitInnerClass(newName, outerName, innerName, access);
    }

    @Override
    public void visitNestMember(String nestMember) {
        if (!controllerPresent()) {
            super.visitNestMember(nestMember);
            return;
        }

        super.visitNestMember(controller.findNameByObfNameOrReturn(nestMember));
    }

    @Override
    public void visitNestHost(String nestHost) {
        if (!controllerPresent()) {
            super.visitNestHost(nestHost);
            return;
        }

        super.visitNestHost(controller.findNameByObfNameOrReturn(nestHost));
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        Log.warn("Visiting not implemented 'visitModule'! name='{}',version='{}'", name, version);
        return super.visitModule(name, access, version);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        Log.warn("Visiting not implemented 'visitTypeAnnotation'! typePath='{}',descriptor='{}'", typePath, descriptor);
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        Log.warn("Visiting not implemented 'visitAttribute'! attr.type='{}'", attribute.type);
        super.visitAttribute(attribute);
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        Log.warn("Visiting not implemented 'visitRecordComponent'! name='{},descriptor='{}'", name, descriptor);
        return super.visitRecordComponent(name, descriptor, signature);
    }
}
