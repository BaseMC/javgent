package javgent.executor.execmodules.jarpacker;

import javgent.executor.execmodules.ExcludedComponent;

import java.util.List;

public abstract class JarPacker {

    public static final String META_INF = "META-INF";
    public static final String META_INF_REPLACEMENT = "META-INF-ORIG";

    protected final List<ExcludedComponent> excludedComponents;

    protected JarPacker(List<ExcludedComponent> excludedComponents) {
        this.excludedComponents = excludedComponents;
    }


    protected String replaceMetaInf(String input) {
        if (!input.startsWith(META_INF))
            return input;

        return input.replaceFirst(META_INF, META_INF_REPLACEMENT);
    }
}
