/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;

/**
 * Dispatching {@link Enhancer} callback. This is identical to the
 * {@link LazyLoader} interface but needs to be separate so that {@code Enhancer}
 * knows which type of code to generate.
 */
public interface Dispatcher extends Callback {
    /**
     * Return the object which the original method invocation should
     * be dispatched. This method is called for <b>every</b> method invocation.
     * @return an object that can invoke the method
     */
    Object loadObject() throws Exception;
}
