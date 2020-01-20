package javgent.executor.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class PatchMethod {

    public PatchMethod() {
    }

    /**
     * Copy constructor
     *
     * @param source
     */
    public PatchMethod(PatchMethod source) {
        this.ObfName = source.ObfName;
        this.Name = source.Name;
        this.ReturnType = source.ReturnType;
        this.Parameters = new ArrayList<>(source.Parameters);
    }

    public String Name;
    public String ObfName;

    public String ReturnType;
    public List<PatchParameter> Parameters = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PatchMethod that = (PatchMethod) o;

        return new EqualsBuilder()
                .append(Name, that.Name)
                .append(ObfName, that.ObfName)
                .append(ReturnType, that.ReturnType)
                .append(Parameters, that.Parameters)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(Name)
                .append(ObfName)
                .append(ReturnType)
                .append(Parameters)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "PatchMethod{" +
                "Name='" + Name + '\'' +
                ", ObfName='" + ObfName + '\'' +
                ", ReturnType='" + ReturnType + '\'' +
                ", Parameters=" + Parameters +
                '}';
    }
}
