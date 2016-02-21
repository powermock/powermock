/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.Attribute;
import org.powermock.api.mockito.repackaged.asm.ClassReader;
import org.powermock.api.mockito.repackaged.asm.ClassVisitor;
import org.powermock.api.mockito.repackaged.cglib.core.ClassGenerator;

public class ClassReaderGenerator implements ClassGenerator {
    private final ClassReader r;
    private final Attribute[] attrs;
    private final int flags;
    
    public ClassReaderGenerator(ClassReader r, int flags) {
        this(r, null, flags);
    }

    public ClassReaderGenerator(ClassReader r, Attribute[] attrs, int flags) {
        this.r = r;
        this.attrs = (attrs != null) ? attrs : new Attribute[0];
        this.flags = flags;
    }
    
    public void generateClass(ClassVisitor v) {
        r.accept(v, attrs, flags);
    }
}
