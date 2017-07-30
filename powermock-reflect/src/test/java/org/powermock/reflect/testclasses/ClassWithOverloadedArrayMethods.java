package org.powermock.reflect.testclasses;

public class ClassWithOverloadedArrayMethods {

    public Object overloaded(Object theObject) {
        return null;
    }

    public Object[] overloaded(Object[] theObjects) { return null; }

    public String overloaded(String theString) {
        return null;
    }

    public String[] overloaded(String[] theString) { return null; }

    public byte[] overloaded(byte[] theBytes) {
        return null;
    }

    public byte overloaded(byte theByte) {
        return 0;
    }

    public int overloaded(int... ints) {   return 0; }

    public int overloaded(int value) { return 0; }


}
