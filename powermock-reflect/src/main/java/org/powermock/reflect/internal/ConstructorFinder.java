package org.powermock.reflect.internal;

import org.powermock.reflect.exceptions.ConstructorNotFoundException;
import org.powermock.reflect.exceptions.TooManyConstructorsFoundException;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

class ConstructorFinder {
    private Class<?> type;
    private Object[] arguments;
    private Class<?> unmockedType;
    private Constructor potentialConstructor;

    ConstructorFinder(Class<?> type, Object... arguments) {
        if (type == null) {
            throw new IllegalArgumentException("Class type cannot be null.");
        }

        this.type = type;
        this.arguments = arguments;
        this.unmockedType = WhiteboxImpl.getUnmockedType(type);

        if (isNestedClass() && arguments != null) {
            addArgumentForNestedClass();
        }
    }

    public java.lang.reflect.Constructor findConstructor() {
        lookupPotentialConstructor();
        throwExceptionIfConstructorWasNotFound();
        return potentialConstructor.getJavaConstructor();
    }

    private void lookupPotentialConstructor() {Set<Constructor> constructors = getDeclaredConstructorsWithoutPowerMockConstructor();

        for (Constructor constructor : constructors) {
            if (constructor.canBeInvokeWith(arguments)) {
                setPotentialConstructor(constructor);
            }

            // if a constructor is found and it has varargs parameters then the constructor will be used even if
            // other constructor is matcher the given arguments. It is done, because when Argument Matchers are used
            // arguments passed to the method are null value and it's imposable to determinate whether parameters
            // match to arguments or not.

            if (isVarArgConstructorFound()){
                return;
            }
        }
    }

    private boolean isVarArgConstructorFound() {return potentialConstructor!=null && potentialConstructor.isVarArg();}

    private void setPotentialConstructor(Constructor constructor) {
        if (potentialConstructor == null) {
            potentialConstructor = constructor;
        }else{
                /*
                       * We've already found a constructor match before, this
                       * means that PowerMock cannot determine which method to
                       * expect since there are two methods with the same name
                       * and the same number of arguments but one is using
                       * wrapper types.
                       */
            throwExceptionWhenMultipleConstructorMatchesFound(new java.lang.reflect.Constructor[]{potentialConstructor.getJavaConstructor(),
                    constructor.getJavaConstructor()});
        }
    }

    public void throwExceptionWhenMultipleConstructorMatchesFound(java.lang.reflect.Constructor[] constructors) {
        if (constructors == null || constructors.length < 2) {
            throw new IllegalArgumentException("Internal error: throwExceptionWhenMultipleConstructorMatchesFound needs at least two constructors.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Several matching constructors found, please specify the argument parameter types so that PowerMock can determine which method you're referring to.\n");
        sb.append("Matching constructors in class ").append(constructors[0].getDeclaringClass().getName())
          .append(" were:\n");

        for (java.lang.reflect.Constructor constructor : constructors) {
            sb.append(constructor.getName()).append("( ");
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (Class<?> paramType : parameterTypes) {
                sb.append(paramType.getName()).append(".class ");
            }
            sb.append(")\n");
        }
        throw new TooManyConstructorsFoundException(sb.toString());
    }

    private void addArgumentForNestedClass() {
        Object[] argumentsForLocalClass = new Object[arguments.length + 1];
        argumentsForLocalClass[0] = unmockedType.getEnclosingClass();
        System.arraycopy(arguments, 0, argumentsForLocalClass, 1, arguments.length);
        arguments = argumentsForLocalClass;
    }

    private boolean isNestedClass() {
        return (unmockedType.isLocalClass() || unmockedType.isAnonymousClass() || unmockedType.isMemberClass())
                       && !Modifier.isStatic(unmockedType.getModifiers());
    }

    private Set<Constructor> getDeclaredConstructorsWithoutPowerMockConstructor() {
        Set<Constructor> constructors = new HashSet<Constructor>();
        for (java.lang.reflect.Constructor<?> constructor : unmockedType.getDeclaredConstructors()) {
            if (!isPowerMockConstructor(constructor.getParameterTypes())) {
                constructors.add(new Constructor(constructor));
            }
        }
        return constructors;
    }

    private boolean isPowerMockConstructor(Class<?>[] parameterTypes) {
        return parameterTypes.length >= 1
                    && parameterTypes[parameterTypes.length - 1].getName().equals(
                "org.powermock.core.IndicateReloadClass");
    }

    private void throwExceptionIfConstructorWasNotFound() {
        if (potentialConstructor == null) {
            String message = "No constructor found in class '" + WhiteboxImpl.getUnmockedType(type).getName() + "' " +
                                     "with "
                                     + "parameter types: [ " + WhiteboxImpl.getArgumentTypesAsString(arguments) + " ].";
            throw new ConstructorNotFoundException(message);
        }
    }
}
