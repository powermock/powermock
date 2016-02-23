/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

import org.powermock.api.mockito.repackaged.asm.ClassVisitor;
import org.powermock.api.mockito.repackaged.cglib.core.ClassGenerator;

public class TransformingClassGenerator implements ClassGenerator {
    private ClassGenerator gen;
    private ClassTransformer t;
    
    public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }
    
    public void generateClass(ClassVisitor v) throws Exception {
        t.setTarget(v);
        gen.generateClass(t);
    }
}
