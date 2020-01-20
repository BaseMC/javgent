package javgent.executor.execmodules;

public class ExcludedComponent {
    private final boolean isDirectory;
    private final String relativeNamePath;

    public ExcludedComponent(String str) {
        this.isDirectory = str.endsWith("/");
        this.relativeNamePath = str;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getRelativeNamePath() {
        return relativeNamePath;
    }

}
