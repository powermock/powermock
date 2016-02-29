package org.powermock.reflect.testclasses;

/**
 *
 */
public class ClassWithOverloadedConstructors {

    private final String name;
    private final boolean bool;
    private final boolean bool1;

    protected ClassWithOverloadedConstructors(String name) {

        this(name, false, false);
    }


    protected ClassWithOverloadedConstructors(boolean bool, String name) {
        this(name, bool, false);
    }

    protected ClassWithOverloadedConstructors(String name, boolean bool, boolean bool1) {
        this.name = name;
        this.bool = bool;
        this.bool1 = bool1;
    }

    public String getName() {
        return name;
    }

    public boolean isBool() {
        return bool;
    }

    public boolean isBool1() {
        return bool1;
    }
}
