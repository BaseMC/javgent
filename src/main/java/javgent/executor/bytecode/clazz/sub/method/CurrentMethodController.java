package javgent.executor.bytecode.clazz.sub.method;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.bytecode.clazz.sub.method.controller.MethodSelector;
import javgent.executor.model.PatchClass;
import javgent.executor.model.PatchMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurrentMethodController {

    private static final Logger Log = LoggerFactory.getLogger(CurrentMethodController.class);

    private Map<String, Map<MethodSelector, PatchMethod>> patchObfNameSelectorMethodMap;
    private CurrentClassController currentClassController;

    public CurrentMethodController(CurrentClassController currentClassController) {
        this.currentClassController = currentClassController;
        generatePatchMethodMap();
    }

    private void generatePatchMethodMap() {
        patchObfNameSelectorMethodMap = currentClassController.getCurrentPatchClass()
                .Methods
                .stream()
                .collect(
                        Collectors.groupingBy(method ->
                                method.ObfName,
                                Collectors.toMap(
                                        MethodSelector::new,
                                        method -> method
                                )
                        )
                );
    }

    public Optional<PatchMethod> findByObfNameAndSelector(String obfName, MethodSelector selector, PatchClass... callerStack) {

        var map = findByObfName(obfName);

        var patchMethod = map.get(selector);
        if(patchMethod != null)
            return Optional.ofNullable(patchMethod);

        var currentPatchClass = getCurrentClassController().getCurrentPatchClass();


        //Check if call is from here
        var callerStackStream = Stream.of(callerStack);
        if(callerStackStream.anyMatch(caller -> caller.equals(currentPatchClass)))
            //We already called from here...
            return Optional.empty();

        //Look into the parents of this class
        var parents = currentPatchClass.getParents();
        if(parents.isEmpty())
            return Optional.empty();

        var callerStackWithMe = Stream
                .concat(Stream.of(callerStack), Stream.of(currentPatchClass))
                .toArray(PatchClass[]::new);

        var parentResults = parents.stream()
                .map(patchParent -> {
                    var patchClassParent = getCurrentClassController().getRegistry().getByObfName(patchParent.ObfName).get();

                    return patchClassParent.getMethodController().findByObfNameAndSelector(
                            obfName,
                            selector,
                            callerStackWithMe);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if(parentResults.isEmpty())
            return Optional.empty();

        if(Log.isDebugEnabled() && parentResults.size() > 1) {
            var logParents = String.join(
                    ", ",
                    parentResults.stream()
                            .map(r -> r.Name + "/" + r.ObfName)
                            .collect(Collectors.toList())
            );

            Log.debug("Got multiple results({}x) from parents: [{}]",
                    parentResults.size(),
                    logParents
            );
        }

        return Optional.ofNullable(parentResults.get(0));
    }

    private Map<MethodSelector, PatchMethod> findByObfName(String obfName) {
        var returnValue = patchObfNameSelectorMethodMap.get(obfName);

        return returnValue != null ? returnValue : new HashMap<>();
    }

    public CurrentClassController getCurrentClassController() {
        return currentClassController;
    }
}
