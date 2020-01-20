package javgent.commodel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class ComPatchMethod {
	public String ReturnType;
	public String Name;
	public List<ComPatchParameter> Parameters = new ArrayList<>();
	
	public String ObfName;

}
