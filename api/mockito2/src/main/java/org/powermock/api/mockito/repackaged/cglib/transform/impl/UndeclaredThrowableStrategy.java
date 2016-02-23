/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform.impl;

import org.powermock.api.mockito.repackaged.cglib.core.ClassGenerator;
import org.powermock.api.mockito.repackaged.cglib.core.DefaultGeneratorStrategy;
import org.powermock.api.mockito.repackaged.cglib.core.TypeUtils;
import org.powermock.api.mockito.repackaged.cglib.transform.ClassTransformer;
import org.powermock.api.mockito.repackaged.cglib.transform.MethodFilter;
import org.powermock.api.mockito.repackaged.cglib.transform.MethodFilterTransformer;
import org.powermock.api.mockito.repackaged.cglib.transform.TransformingClassGenerator;

/**
 * A {@link org.powermock.api.mockito.repackaged.cglib.core.GeneratorStrategy} suitable for use with {@link org.powermock.api.mockito.repackaged.cglib.proxy.Enhancer} which
 * causes all undeclared exceptions thrown from within a proxied method to be wrapped
 * in an alternative exception of your choice.
 */
public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy {
    private static final MethodFilter TRANSFORM_FILTER = new MethodFilter() {
        public boolean accept(int access, String name, String desc, String signature, String[] exceptions) {
            return !TypeUtils.isPrivate(access) && name.indexOf('$') < 0;
        }
    };
    private ClassTransformer t;
    
    /**
     * Create a new instance of this strategy.
     * @param wrapper a class which extends either directly or
     * indirectly from <code>Throwable</code> and which has at least one
     * constructor that takes a single argument of type
     * <code>Throwable</code>, for example
     * <code>java.lang.reflect.UndeclaredThrowableException.class</code>
     */
    public UndeclaredThrowableStrategy(Class wrapper) {
        t = new UndeclaredThrowableTransformer(wrapper);
        t = new MethodFilterTransformer(TRANSFORM_FILTER, t);
    }

    protected ClassGenerator transform(ClassGenerator cg) throws Exception {
        return new TransformingClassGenerator(cg, t);
    }
}

