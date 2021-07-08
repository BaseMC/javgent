package javgent;

import org.objectweb.asm.Opcodes;

/**
 * ASM Actual Controller/Configuration
 * Contains general methods that are used in the context of ASM
 */
public final class ASMAC {
    private ASMAC() {
        //No Impl pls
    }

    public static final int ASM_VERSION = Opcodes.ASM9;

    public static final String JAVA_PACKAGE_SEPARATOR = ".";
    public static final String CLASS_PACKAGE_SEPARATOR = "/";

    public static String toPackageJavaConform(String input) {
        return input.replace(CLASS_PACKAGE_SEPARATOR, JAVA_PACKAGE_SEPARATOR);
    }

    public static String toPackageClassConform(String input) {
        return input.replace(JAVA_PACKAGE_SEPARATOR, CLASS_PACKAGE_SEPARATOR);
    }
}
