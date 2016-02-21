/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */

package org.powermock.api.mockito.repackaged.cglib.proxy;

import org.powermock.api.mockito.repackaged.cglib.core.CodeGenerationException;

/**
 * Used by {@link Proxy} as a replacement for <code>java.lang.reflect.UndeclaredThrowableException</code>.
 * @author Juozas Baliuka
 */
public class UndeclaredThrowableException extends CodeGenerationException {
    /**
     * Creates a new instance of <code>UndeclaredThrowableException</code> without detail message.
     */
    public UndeclaredThrowableException(Throwable t) {
        super(t);
    }
    
    public Throwable getUndeclaredThrowable() {
        return getCause();
    }
}
