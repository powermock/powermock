/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.Type;

public class Local
{
    private Type type;
    private int index;
    
    public Local(int index, Type type) {
        this.type = type;
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }

    public Type getType() {
        return type;
    }
}
