package javgent.executor.execmodules;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class ExecutorConfig {
	public String srcFile;
	public String patchFileDir;
	public String targetFile;

	public String mappingFile;

	public List<String> excludeDoNotPackComponents = new ArrayList<>();

	public String workDir = "workDir";
	public boolean readInputDirectly = false; //Read input and patchFiles directly
	public boolean cleanWorkDir = false;

	public boolean inMemoryMode = true;
}
