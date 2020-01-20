package javgent.executor.bytecode.clazz.util;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.sub.method.visitor.MethodSelectorVistiorDetector;

import java.util.Optional;

public class RemoteMethodResolver {

    private RemoteMethodResolver() {
        //NoImpl pls
    }

    public static Optional<RemoteMethodResolverResult> resolve(CurrentClassController currentClassController, String owner, String obfName, String desc) {

        var optController = currentClassController.findPatchControllerByObfName(owner);

        if (optController.isEmpty())
            return Optional.empty();

        var result = new RemoteMethodResolverResult();


        var remoteController = optController.get();

        result.Owner = currentClassController.findNameByObfNameOrReturn(owner);

        if(obfName == null && desc == null)
            return Optional.ofNullable(result);

        var selector = MethodSelectorVistiorDetector.findSelector(remoteController, desc, null);

        var optRemotePatchMethod = remoteController.getMethodController().findByObfNameAndSelector(obfName, selector);
        if (optRemotePatchMethod.isPresent()) {
            var remotePatchMethod = optRemotePatchMethod.get();

            result.Name = Optional.ofNullable(remotePatchMethod.Name);
        }

        return Optional.ofNullable(result);
    }

    @SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
    public static class RemoteMethodResolverResult {
        public String Owner;
        public Optional<String> Name = Optional.empty();
    }
}
