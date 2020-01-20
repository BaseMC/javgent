package javgent.executor.bytecode.clazz.sub.method.util;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodDescriptorWriter;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodInsSignatureWriter;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodSignatureWriter;
import javgent.executor.bytecode.clazz.util.RemoteMethodResolver;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class DynamicHelper {

    private static final Logger Log = LoggerFactory.getLogger(DynamicHelper.class);

    private CurrentClassController controller;

    public DynamicHelper(CurrentClassController controller) {
        this.controller = controller;
    }

    public Object[] handleBootStrapArgs(Object... bootstrapMethodArguments) {
        /* @param bootstrapMethodArguments the bootstrap method constant arguments. Each argument must be
         *     an {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String}, {@link
         *     Type}, {@link Handle} or {@link ConstantDynamic} value. This method is allowed to modify
         *     the content of the array so a caller should expect that this array may change.
         */
        return Stream.of(bootstrapMethodArguments)
                .map(this::handleArg)
                .toArray();
    }

    public Object handleArg(Object obj) {
        if (obj instanceof Integer ||
                obj instanceof Float ||
                obj instanceof Double ||
                obj instanceof Long ||
                obj instanceof String)
            return obj;
        else if (obj instanceof Type) {
            return handleType((Type) obj);
        } else if (obj instanceof Handle) {
            return handleHandle((Handle) obj);
        } else if (obj instanceof ConstantDynamic) {
            return handleConstantDynamic((ConstantDynamic) obj);
        } else {
            Log.warn("Unknown type '{}'", obj.getClass());
            return obj;
        }
    }

    public Type handleType(Type type) {
        return Type.getType(
                MethodInsSignatureWriter.convert(
                        controller,
                        type.toString()
                )
        );
    }

    public Handle handleHandle(Handle handle) {
        if (handle == null)
            return null;

        var handleOwner = handle.getOwner();
        var handleName = handle.getName();
        var handleDesc = handle.getDesc();

        var optRemoteMethodResolver = RemoteMethodResolver.resolve(
                controller,
                handleOwner,
                handleName,
                handleDesc
        );

        if (optRemoteMethodResolver.isPresent()) {
            var remoteMethodResolver = optRemoteMethodResolver.get();
            handleOwner = remoteMethodResolver.Owner;

            var optName = remoteMethodResolver.Name;
            if (optName.isPresent())
                handleName = optName.get();

        }

        if(handleDesc != null)
            handleDesc = MethodSignatureWriter.convert(controller,handleDesc);

        return new Handle(
                handle.getTag(),
                handleOwner,
                handleName,
                handleDesc,
                handle.isInterface()
        );
    }


    public ConstantDynamic handleConstantDynamic(ConstantDynamic constantDynamic) {

        String newName = constantDynamic.getName();

        String newDescriptor = MethodDescriptorWriter.convert(controller, constantDynamic.getDescriptor());

        var newBootstrapMethodHandle = handleHandle(constantDynamic.getBootstrapMethod());

        var bootstrapMethodArguments = new Object[constantDynamic.getBootstrapMethodArgumentCount()];
        for (var i = 0; i < constantDynamic.getBootstrapMethodArgumentCount(); i++)
            bootstrapMethodArguments[i] = constantDynamic.getBootstrapMethodArgument(i);

        var newBootstrapMethodArguments = handleBootStrapArgs(bootstrapMethodArguments);

        return new ConstantDynamic(newName, newDescriptor, newBootstrapMethodHandle, newBootstrapMethodArguments);
    }
}
