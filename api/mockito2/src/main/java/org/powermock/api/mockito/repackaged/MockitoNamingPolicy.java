/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged;

import org.powermock.api.mockito.repackaged.cglib.core.DefaultNamingPolicy;

class MockitoNamingPolicy extends DefaultNamingPolicy {
    
    public static final MockitoNamingPolicy INSTANCE = new MockitoNamingPolicy(); 
    
    @Override
    protected String getTag() {
        return "ByMockitoWithCGLIB";
    }
}