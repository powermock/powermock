/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.Label;

public class Block
{
    private CodeEmitter e;
    private Label start;
    private Label end;

    public Block(CodeEmitter e) {
        this.e = e;
        start = e.mark();
    }

    public CodeEmitter getCodeEmitter() {
        return e;
    }

    public void end() {
        if (end != null) {
            throw new IllegalStateException("end of label already set");
        }
        end = e.mark();
    }
    
    public Label getStart() {
        return start;
    }

    public Label getEnd() {
        return end;
    }
}
