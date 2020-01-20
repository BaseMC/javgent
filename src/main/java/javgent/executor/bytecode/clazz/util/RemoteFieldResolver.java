package javgent.executor.bytecode.clazz.util;

import javgent.executor.bytecode.clazz.CurrentClassController;

import java.util.Optional;

public class RemoteFieldResolver {

    private RemoteFieldResolver() {
        //NoImpl pls
    }


    public static Optional<RemoteFieldResolverResult> resolve(CurrentClassController currentClassController, String owner, String name) {

        var optController = currentClassController.findPatchControllerByObfName(owner);
        if(optController.isEmpty())
            return Optional.empty();

        var result = new RemoteFieldResolverResult();

        result.Owner = currentClassController.findNameByObfNameOrReturn(owner);

        var remoteController = optController.get();

        var optRemotePatchField = remoteController.getFieldsController().findByObfName(name);
        if(optRemotePatchField.isPresent()) {
            result.Name = Optional.ofNullable(optRemotePatchField.get().Name);
        }


        return Optional.ofNullable(result);
    }

    @SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
    public static class RemoteFieldResolverResult {
        public String Owner;
        public Optional<String> Name = Optional.empty();
    }
}
