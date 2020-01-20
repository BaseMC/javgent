package javgent.executor.bytecode.clazz.sub.field;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.model.PatchClass;
import javgent.executor.model.PatchField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurrentFieldsController {

    private static final Logger Log = LoggerFactory.getLogger(CurrentFieldsController.class);

    private Map<String, PatchField> patchFieldMap;
    private CurrentClassController currentClassController;

    public CurrentFieldsController(CurrentClassController currentClassController) {
        this.currentClassController = currentClassController;
        generatePatchFieldMap();
    }

    private void generatePatchFieldMap() {
        patchFieldMap =
                currentClassController
                        .getCurrentPatchClass()
                        .Fields
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        field -> field.ObfName,
                                        field -> field
                                )
                        );
    }

    public String findNameByObfNameOrReturn(String obfName) {
        var opt = findByObfName(obfName);
        if (!opt.isPresent())
            return obfName;
        return opt.get().Name;
    }


    public Optional<PatchField> findByObfName(String obfName, PatchClass... callerStack) {

        var patchField = patchFieldMap.get(obfName);

        if (patchField != null)
            return Optional.ofNullable(patchField);

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

                    return patchClassParent.getFieldsController().findByObfName(
                            obfName,
                            callerStackWithMe);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if(parentResults.isEmpty())
            return Optional.empty();

        if(parentResults.size() > 1)
            Log.debug("Got multiple results({}x) from parents: [{}]",
                    parentResults.size(),
                    String.join(
                            ", ",
                            parentResults.stream()
                                    .map(r -> r.Name+"/"+r.ObfName)
                                    .collect(Collectors.toList())
                    )
            );

        return Optional.ofNullable(parentResults.get(0));
    }

    public CurrentClassController getCurrentClassController() {
        return currentClassController;
    }
}
