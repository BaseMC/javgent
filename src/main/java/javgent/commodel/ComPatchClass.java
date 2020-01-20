package javgent.commodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class ComPatchClass {
	
	public String Name;
	public String ObfName;
	
	public List<ComPatchMethod> Methods = new ArrayList<>();
	public List<ComPatchField> Fields = new ArrayList<>();


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		ComPatchClass that = (ComPatchClass) o;

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
}
