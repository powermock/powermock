/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.reflect;

import java.lang.reflect.Member;

abstract public class FastMember
{
    protected FastClass fc;
    protected Member member;
    protected int index;

    protected FastMember(FastClass fc, Member member, int index) {
        this.fc = fc;
        this.member = member;
        this.index = index;
    }

    abstract public Class[] getParameterTypes();
    abstract public Class[] getExceptionTypes();

    public int getIndex() {
        return index;
    }

    public String getName() {
        return member.getName();
    }

    public Class getDeclaringClass() {
        return fc.getJavaClass();
    }

    public int getModifiers() {
        return member.getModifiers();
    }

    public String toString() {
        return member.toString();
    }

    public int hashCode() {
        return member.hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof FastMember)) {
            return false;
        }
        return member.equals(((FastMember)o).member);
    }
}
