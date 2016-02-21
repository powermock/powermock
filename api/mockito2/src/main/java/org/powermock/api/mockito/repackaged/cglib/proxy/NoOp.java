/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.proxy;

/**
 * Methods using this {@link Enhancer} callback will delegate directly to the
 * default (super) implementation in the base class.
 */
public interface NoOp extends Callback
{
    /**
     * A thread-safe singleton instance of the <code>NoOp</code> callback.
     */
    public static final NoOp INSTANCE = new NoOp() { };
}
