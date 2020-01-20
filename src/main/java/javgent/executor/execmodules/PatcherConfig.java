package javgent.executor.execmodules;

import javgent.executor.model.PatchClass;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class PatcherConfig {
	public Path modifiedClassesDir;

	public boolean inMemory;

	public Set<PatchClass> classPatchFiles = new HashSet<>();
	public Set<ClassInfo> classInfos;
}
