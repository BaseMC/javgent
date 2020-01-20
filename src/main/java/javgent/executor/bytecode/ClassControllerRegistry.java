package javgent.executor.bytecode;

import javgent.executor.bytecode.clazz.CurrentClassController;
import javgent.executor.model.PatchClass;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassControllerRegistry {

    private Map<String, CurrentClassController> comPatchClassesObfNameResolver;

    public ClassControllerRegistry(Map<String, PatchClass> obfPatchClasses) {
        this.comPatchClassesObfNameResolver =
                obfPatchClasses.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new CurrentClassController(
                                    this,
                                    e.getValue()
                            )
                    ));
    }

    public Optional<CurrentClassController> getByObfName(String name) {
        return Optional.ofNullable(comPatchClassesObfNameResolver.get(name));
    }

}
