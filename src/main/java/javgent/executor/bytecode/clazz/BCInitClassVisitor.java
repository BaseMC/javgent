package javgent.executor.bytecode.clazz;

import javgent.ASMAC;
import javgent.executor.bytecode.ClassControllerRegistry;
import javgent.executor.model.PatchMethod;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class BCInitClassVisitor extends ClassVisitor {

    private static final Logger Log = LoggerFactory.getLogger(BCInitClassVisitor.class);

    private ClassControllerRegistry registry;
    private CurrentClassController controller;


    public BCInitClassVisitor( ClassControllerRegistry registry) {
        super(ASMAC.ASM_VERSION, null);
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

        if(!controllerPresent())
            return;

        installSuperName(superName);
        installInterfaces(interfaces);

        if((access & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM) {
            controller.getCurrentPatchClass().IsEnum = true;

            var methods = controller.getCurrentPatchClass().Methods;

            //Fix enum calls
            var toFix = methods
                    .stream()
                    .filter(m ->
                            m.ObfName.equals("<init>") &&
                            m.Parameters.size() >= 2 &&
                            m.Parameters.get(0).Type.equals("java/lang/String") &&
                            m.Parameters.get(1).Type.equals("int"))
                    .collect(Collectors.toList());

            Log.debug("Trying to add {}x correction-Method(s) to enum '{}/{}'",
                    toFix.size(),
                    controller.getCurrentPatchClass().ObfName,
                    controller.getCurrentPatchClass().Name);

            for (var m : toFix) {
               var cloned = new PatchMethod(m);

               cloned.Parameters.remove(0);
               cloned.Parameters.remove(0);

               if(!methods.add(cloned))
                   Log.warn("Failed to add correction-Method for enum '{}/{}': {}/{}",
                           controller.getCurrentPatchClass().ObfName,
                           controller.getCurrentPatchClass().Name,
                           cloned.ObfName,
                           cloned.Name);
            }
        }
    }

    public void installSuperName(String superName) {
        if (superName == null)
            return;

        var optSuperController = controller.findPatchControllerByObfName(superName);
        if(optSuperController.isEmpty())
            return;

        var superController = optSuperController.get();

        controller.getCurrentPatchClass().SuperClazz = superController.getCurrentPatchClass();
    }

    public void installInterfaces(String[] interfaces) {
        Arrays.stream(interfaces)
                .forEach(interf -> {
                    var optInterfController = controller.findPatchControllerByObfName(interf);
                    if(optInterfController.isPresent()) {

                        var interfController = optInterfController.get();

                        controller.getCurrentPatchClass().Interfaces.add(interfController.getCurrentPatchClass());
                    }
                });
    }
}
