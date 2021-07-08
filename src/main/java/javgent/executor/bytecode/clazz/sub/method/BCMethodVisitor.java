package javgent.executor.bytecode.clazz.sub.method;

import javgent.ASMAC;
import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.sub.method.util.DynamicHelper;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodDescriptorWriter;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodSignatureWriter;
import javgent.executor.bytecode.clazz.sub.method.visitor.TypeSignatureWriter;
import javgent.executor.bytecode.clazz.util.RemoteFieldResolver;
import javgent.executor.bytecode.clazz.util.RemoteMethodResolver;
import javgent.executor.bytecode.clazz.writers.ClassDescriptorWriter;
import javgent.executor.bytecode.clazz.writers.ClassSignatureWriter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCMethodVisitor extends MethodVisitor {

    private static final Logger Log = LoggerFactory.getLogger(BCMethodVisitor.class);

    private ModifierForMethod modifier;

    private CurrentClassController getCurrentClassController() {
        return modifier.getController().getCurrentClassController();
    }

    public BCMethodVisitor(ModifierForMethod modifier, MethodVisitor visitMethod) {
        super(ASMAC.ASM_VERSION, visitMethod);
        this.modifier = modifier;
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {

        if (descriptor != null) {
            descriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);
        }

        if (signature != null) {
            signature = ClassSignatureWriter.convert(getCurrentClassController(), signature);
        }

        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {

        var optionalMethodFieldResolverResult = RemoteFieldResolver.resolve(getCurrentClassController(), owner, name);
        if (optionalMethodFieldResolverResult.isPresent()) {
            var methodFieldResolverResult = optionalMethodFieldResolverResult.get();

            var optName = methodFieldResolverResult.Name;
            if (optName.isPresent())
                name = optName.get();
            else
                Log.info("Failed to find name: {}, {}, {} ", owner, name, descriptor);

            owner = methodFieldResolverResult.Owner;
        } else
            Log.debug("Not found: {}, {}, {} ", owner, name, descriptor);

        if (descriptor != null)
            descriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);

        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

        var optionalMethodMethodResolverResult = RemoteMethodResolver.resolve(getCurrentClassController(), owner, name, descriptor);
        if (optionalMethodMethodResolverResult.isPresent()) {
            var methodFieldResolverResult = optionalMethodMethodResolverResult.get();

            var optName = methodFieldResolverResult.Name;
            if (optName.isPresent())
                name = optName.get();
            else if (name.length() < 3)
                Log.info("Failed to find name (name was < 3; maybe not an problem): {}, {}, {} ", owner, name, descriptor);
            else
                Log.debug("Failed to find name: {}, {}, {} ", owner, name, descriptor);

            owner = methodFieldResolverResult.Owner;
        } else
            Log.debug("Not found: {}, {}, {} ", owner, name, descriptor);

        if (descriptor != null)
            descriptor = MethodSignatureWriter.convert(getCurrentClassController(), descriptor);

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {

        String newDescriptor = MethodSignatureWriter.convert(getCurrentClassController(), descriptor);

        var helper = new DynamicHelper(getCurrentClassController());


        var newBootstrapMethodHandle = helper.handleHandle(bootstrapMethodHandle);

        var newBootstrapMethodArguments = helper.handleBootStrapArgs(bootstrapMethodArguments);

        super.visitInvokeDynamicInsn(name, newDescriptor, newBootstrapMethodHandle, newBootstrapMethodArguments);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        var newType = TypeSignatureWriter.convert(this.getCurrentClassController(), type);

        super.visitTypeInsn(opcode, newType);
    }

    @Override
    public void visitLdcInsn(Object value) {

        /* @param value the constant to be loaded on the stack. This parameter must be a non null {@link
         *     Integer}, a {@link Float}, a {@link Long}, a {@link Double}, a {@link String}, a {@link
         *     Type} of OBJECT or ARRAY sort for {@code .class} constants, for classes whose version is
         *     49, a {@link Type} of METHOD sort for MethodType, a {@link Handle} for MethodHandle
         *     constants, for classes whose version is 51 or a {@link ConstantDynamic} for a constant
         *     dynamic for classes whose version is 55.
         */
        var helper = new DynamicHelper(getCurrentClassController());

        var newValue = helper.handleArg(value);

        super.visitLdcInsn(newValue);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

        if (type != null) {
            var newType = getCurrentClassController().findNameByObfNameOrReturn(type);
            super.visitTryCatchBlock(start, end, handler, newType);
            return;
        }

        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {

        var newDesc = MethodDescriptorWriter.convert(getCurrentClassController(), descriptor);

        super.visitMultiANewArrayInsn(newDesc, numDimensions);
    }


    //Annotation-Stuff
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String newDescriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);

        return super.visitAnnotation(newDescriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        String newDescriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);

        return super.visitTryCatchAnnotation(typeRef, typePath, newDescriptor, visible);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        String newDescriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);

        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, newDescriptor, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        String newDescriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);

        return super.visitParameterAnnotation(parameter, newDescriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        String newDescriptor = ClassDescriptorWriter.convert(getCurrentClassController(), descriptor);

        return super.visitTypeAnnotation(typeRef, typePath, newDescriptor, visible);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        Log.warn("Visiting not implemented 'visitInsnAnnotation! descriptor='{}'", descriptor);
        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
    }

    //----

    @Override
    public void visitAttribute(Attribute attribute) {
        Log.warn("Visiting not implemented 'visitAttribute'! attr.type='{}'", attribute.type);
        super.visitAttribute(attribute);
    }

    @Override
    public void visitParameter(String name, int access) {
        Log.warn("Visiting not implemented 'visitParameter'! name='{}'", name);
        super.visitParameter(name, access);
    }

}
