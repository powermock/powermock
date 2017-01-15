/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;

/**
 * Lazy-loading {@link Enhancer} callback.
 */
public interface LazyLoader extends Callback {
    /**
     * Return the object which the original method invocation should be
     * dispatched. Called as soon as the first lazily-loaded method in
     * the enhanced instance is invoked. The same object is then used
     * for every future method call to the proxy instance.
     * @return an object that can invoke the method
     */
    Object loadObject() throws Exception;
}
