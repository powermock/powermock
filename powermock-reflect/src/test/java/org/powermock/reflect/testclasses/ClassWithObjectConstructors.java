package org.powermock.reflect.testclasses;

/**
 *
 */
public class ClassWithObjectConstructors {

    private final Object name;

    protected ClassWithObjectConstructors(Object name) {

        this.name = name;
    }

    public Object getName() {
        return name;
    }

}
