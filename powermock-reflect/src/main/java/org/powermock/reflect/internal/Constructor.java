package org.powermock.reflect.internal;

class Constructor {

    private final Class<?>[] parameterTypes;
    private java.lang.reflect.Constructor constructor;
    private boolean isVarArgs;

    Constructor(java.lang.reflect.Constructor constructor) {
        this.constructor = constructor;
        this.parameterTypes = constructor.getParameterTypes();
        this.isVarArgs = constructor.isVarArgs();
    }

    boolean canBeInvokeWith(Object[] arguments) {
        return new ParametersMatcher(isVarArgs, parameterTypes, arguments).match();
    }

    public java.lang.reflect.Constructor<?> getJavaConstructor() {
        return constructor;
    }

    public boolean isVarArg() {
        return isVarArgs;
    }
}
