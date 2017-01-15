/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.transform.impl;

public interface FieldProvider {
    
    String[] getFieldNames();
    
    Class[]  getFieldTypes();
    
    void setField(int index, Object value);
    
    Object getField(int index);
    
    
    void setField(String name, Object value);
    
    Object getField(String name);
    
    
}
