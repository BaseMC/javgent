package javgent.executor.execmodules;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.File;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class ExtractedJarFileEntryInfo extends FileEntryInfo {
    public File File;
    public boolean IsClass;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExtractedJarFileEntryInfo that = (ExtractedJarFileEntryInfo) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(IsClass, that.IsClass)
                .append(File, that.File)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(File)
                .append(IsClass)
                .toHashCode();
    }
}
