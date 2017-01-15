/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.Label;

public interface ObjectSwitchCallback {
    void processCase(Object key, Label end) throws Exception;
    void processDefault() throws Exception;
}

