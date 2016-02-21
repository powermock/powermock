/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.Type;

/**
 * A representation of a method signature, containing the method name,
 * return type, and parameter types.
 */
public class Signature {
    private String name;
    private String desc;

    public Signature(String name, String desc) {
        // TODO: better error checking
        if (name.indexOf('(') >= 0) {
            throw new IllegalArgumentException("Name '" + name + "' is invalid");
        }
        this.name = name;
        this.desc = desc;
    }

    public Signature(String name, Type returnType, Type[] argumentTypes) {
        this(name, Type.getMethodDescriptor(returnType, argumentTypes));
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return desc;
    }

    public Type getReturnType() {
        return Type.getReturnType(desc);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(desc);
    }

    public String toString() {
        return name + desc;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Signature))
            return false;
        Signature other = (Signature)o;
        return name.equals(other.name) && desc.equals(other.desc);
    }

    public int hashCode() {
        return name.hashCode() ^ desc.hashCode();
    }
}
