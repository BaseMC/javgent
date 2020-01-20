package javgent.executor.execmodules;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

@SuppressWarnings({"squid:ClassVariableVisibilityCheck", "squid:S00116"})
public class FileEntryInfo {
    public String RelativeNamePath;
    public boolean IsDirectory = false;
    public byte[] Data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FileEntryInfo that = (FileEntryInfo) o;

        return new EqualsBuilder()
                .append(RelativeNamePath, that.RelativeNamePath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(RelativeNamePath)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "FileEntryInfo{" +
                "RelativeNamePath='" + RelativeNamePath + '\'' +
                ", IsDirectory=" + IsDirectory +
                ", Data=" + Arrays.toString(Data) +
                '}';
    }
}
