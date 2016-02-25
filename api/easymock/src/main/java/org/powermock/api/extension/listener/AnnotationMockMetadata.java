/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.api.extension.listener;

import org.powermock.reflect.Whitebox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class AnnotationMockMetadata {
    private final Class<?> type;
    private final Method[] methods;
    private final String qualifier;
    private final Class<? extends Annotation> annotation;
    private final Annotation annotationInstance;
    private Object mock;
    
    public AnnotationMockMetadata(Class<? extends Annotation> annotation, Field field) throws
            Exception {
        this.annotation = annotation;
        this.annotationInstance = field.getAnnotation(annotation);
        this.type = field.getType();
        this.methods = getMethod();
        this.qualifier = findQualifier();
    }

    private String findQualifier() {
        String fieldName = "";
        try {
            fieldName = Whitebox.invokeMethod(annotationInstance, "fieldName");
        } catch (Exception e) {
            // do nothing, because it means that Mock annotation doesn't support qualifier. S
            // ee org.easymock.Mock.fieldName
        }

        if (fieldName.length() == 0) {
            return "";
        } else {
            return fieldName;
        }
    }

    public String getQualifier() {
        return qualifier;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public Class<?> getType() {
        return type;
    }

    public Method[] getMethods() {
        return methods;
    }

    private Method[] getMethod() throws Exception {

        final String[] value = Whitebox.invokeMethod(annotationInstance, "value");
        if (value.length != 1 || !"".equals(value[0])) {
            return Whitebox.getMethods(type, value);
        }
        return null;
    }

    public Object getMock() {
        return mock;
    }

    public void setMock(Object mock) {
        this.mock = mock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AnnotationMockMetadata that = (AnnotationMockMetadata) o;

        if (type != null ? !type.equals(that.type) : that.type != null) { return false; }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(methods, that.methods) && (qualifier != null ? qualifier.equals(that.qualifier) : that.qualifier == null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(methods);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }
}
