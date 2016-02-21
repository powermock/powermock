/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged;

import org.powermock.api.mockito.repackaged.cglib.proxy.Callback;
import org.powermock.api.mockito.repackaged.cglib.proxy.NoOp;

import java.io.Serializable;

/**
 * Offer a Serializable implementation of the NoOp CGLIB callback.
 */
class SerializableNoOp implements NoOp, Serializable {

    public static final Callback SERIALIZABLE_INSTANCE = new SerializableNoOp();
    private static final long serialVersionUID = 7434976328690189159L;

}