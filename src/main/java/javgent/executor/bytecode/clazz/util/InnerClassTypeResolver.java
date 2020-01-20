package javgent.executor.bytecode.clazz.util;

import javgent.executor.bytecode.clazz.CurrentClassController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InnerClassTypeResolver {

    private static final Logger Log = LoggerFactory.getLogger(InnerClassTypeResolver.class);

    private InnerClassTypeResolver() {
    }

    public static String resolve(CurrentClassController controller, String endClassName) {

        if(endClassName == null || endClassName.isEmpty())
            return endClassName;

        var obfBaseClassName = controller.getCurrentPatchClass().ObfName;

        var oldClassName = controller.getCurrentPatchClass().ObfName + "$" + endClassName;
        var newClassName = controller.findNameByObfNameOrReturn(oldClassName);

        if(newClassName.equals(oldClassName))
            return endClassName;

        var index = newClassName.indexOf('$');
        if (index == -1) {
            Log.warn("Failed to find innerClassName: No $ in base='{}'/'{}'; innerClassName='{}'",
                    controller.findNameByObfNameOrReturn(obfBaseClassName),
                    obfBaseClassName,
                    endClassName);
            return endClassName;
        }

        return newClassName.substring(index+1);

    }
}
