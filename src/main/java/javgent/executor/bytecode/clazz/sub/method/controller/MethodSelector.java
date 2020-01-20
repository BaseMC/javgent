package javgent.executor.bytecode.clazz.sub.method.controller;

import javgent.executor.model.PatchMethod;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodSelector {

    private static final String VOID_NAME = Type.getType(Void.TYPE).getClassName();

    private String returnType = VOID_NAME;
    private List<String> parameterTypes = new ArrayList<>();

    public MethodSelector() {

    }

    public MethodSelector(PatchMethod patchMethod) {
        this.returnType = patchMethod.ReturnType;
        this.parameterTypes =
                patchMethod
                        .Parameters
                        .stream()
                        .map(comPar -> comPar.Type)
                        .collect(Collectors.toList());
    }


    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void addParameterType(String type) {

        if (type == null)
            type = VOID_NAME;

        this.parameterTypes.add(type);
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MethodSelector that = (MethodSelector) o;

        return new EqualsBuilder()
                .append(returnType, that.returnType)
                .append(parameterTypes, that.parameterTypes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(returnType)
                .append(parameterTypes)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "MethodSelector{" +
                "returnType='" + returnType + '\'' +
                ", parameterTypes=" + parameterTypes +
                '}';
    }
}
