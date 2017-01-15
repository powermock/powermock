/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform;

public interface MethodFilter {
    // TODO: pass class name too?
    boolean accept(int access, String name, String desc, String signature, String[] exceptions);
}
