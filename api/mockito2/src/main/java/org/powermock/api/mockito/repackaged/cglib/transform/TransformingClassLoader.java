/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.ClassReader;
import org.powermock.api.mockito.repackaged.cglib.core.ClassGenerator;

public class TransformingClassLoader extends AbstractClassLoader {
    private ClassTransformerFactory t;
    
    public TransformingClassLoader(ClassLoader parent, ClassFilter filter, ClassTransformerFactory t) {
        super(parent, parent, filter);
        this.t = t;
    }

    protected ClassGenerator getGenerator(ClassReader r) {
        ClassTransformer t2 = (ClassTransformer)t.newInstance();
        return new TransformingClassGenerator(super.getGenerator(r), t2);
    }
}
