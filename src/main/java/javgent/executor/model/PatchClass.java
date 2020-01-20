package javgent.executor.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class PatchClass {
	
	public String Name;
	public String ObfName;
	
	public Set<PatchMethod> Methods = new HashSet<>();
	public List<PatchField> Fields = new ArrayList<>();

	public PatchClass SuperClazz;
	public Set<PatchClass> Interfaces = new HashSet<>();

	public boolean IsEnum = false;

	public Set<PatchClass> getParents() {
		var parents = new HashSet<>(this.Interfaces);
		if(this.SuperClazz != null)
			parents.add(this.SuperClazz);
		return parents;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		PatchClass that = (PatchClass) o;

		return new EqualsBuilder()
				.append(Name, that.Name)
				.append(ObfName, that.ObfName)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(Name)
				.append(ObfName)
				.toHashCode();
	}

	@Override
	public String toString() {
		return "PatchClass{" +
				"Name='" + Name + '\'' +
				", ObfName='" + ObfName + '\'' +
				", Methods=" + Methods +
				", Fields=" + Fields +
				", SuperClazz=" + SuperClazz +
				", Interfaces=" + Interfaces +
				", IsEnum=" + IsEnum +
				'}';
	}
}
