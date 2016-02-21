/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform.impl;

import org.powermock.api.mockito.repackaged.asm.Type;

public interface InterceptFieldFilter {
    boolean acceptRead(Type owner, String name);
    boolean acceptWrite(Type owner, String name);
}
