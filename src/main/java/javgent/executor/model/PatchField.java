package javgent.executor.model;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class PatchField {
	public String Type;
	public String Name;
	
	public String ObfName;

	@Override
	public String toString() {
		return "PatchField{" +
				"Type='" + Type + '\'' +
				", Name='" + Name + '\'' +
				", ObfName='" + ObfName + '\'' +
				'}';
	}
}
