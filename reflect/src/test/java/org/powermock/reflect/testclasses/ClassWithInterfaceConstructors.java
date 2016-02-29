package org.powermock.reflect.testclasses;

/**
 *
 */
public class ClassWithInterfaceConstructors {


    private final ConstructorInterface constructorInterface;

    public ClassWithInterfaceConstructors(ConstructorInterface constructorInterface) {this.constructorInterface = constructorInterface;}

    public String getValue() {
        return constructorInterface.getValue();
    }

    public interface ConstructorInterface {
        String getValue();
    }

    public static class ConstructorInterfaceImpl implements ConstructorInterface {
        private final String value;

        public ConstructorInterfaceImpl(String value) {this.value = value;}

        @Override
        public String getValue() {
            return value;
        }
    }
}
