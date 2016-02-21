/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;

/**
 * {@link Enhancer} callback that simply returns the value to return
 * from the proxied method. No information about what method
 * is being called is available to the callback, and the type of
 * the returned object must be compatible with the return type of
 * the proxied method. This makes this callback primarily useful
 * for forcing a particular method (through the use of a {@link CallbackFilter}
 * to return a fixed value with little overhead.
 */
public interface FixedValue extends Callback {
    /**
     * Return the object which the original method invocation should
     * return. This method is called for <b>every</b> method invocation.
     * @return an object matching the type of the return value for every
     * method this callback is mapped to
     */
    Object loadObject() throws Exception;
}
