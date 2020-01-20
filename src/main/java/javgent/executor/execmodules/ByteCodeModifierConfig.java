package javgent.executor.execmodules;

import javgent.executor.model.PatchClass;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("squid:ClassVariableVisibilityCheck")
public class ByteCodeModifierConfig {
	public Map<String, PatchClass> patchClassesMap;
	public Set<ClassInfo> classSet;

	public Path modifiedClassesDir;

	public boolean inMemory;
}
