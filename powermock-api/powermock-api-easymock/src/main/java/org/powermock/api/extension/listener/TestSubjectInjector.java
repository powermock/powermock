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

import org.easymock.Mock;
import org.easymock.TestSubject;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * The class injects mocks created with {@link Mock}, {@link org.powermock.api.easymock.annotation.Mock}
 * and {@link org.powermock.core.classloader.annotations.Mock} to fields of objects which is annotated with {@link TestSubject}
 * @see TestSubject
 * @since 1.6.5
 */
@SuppressWarnings({"deprecation", "WeakerAccess"})
class TestSubjectInjector {

    private final Object testInstance;
    private final AnnotationGlobalMetadata globalMetadata;

    public TestSubjectInjector(Object testInstance, AnnotationGlobalMetadata globalMetadata) {

        this.testInstance = testInstance;
        this.globalMetadata = globalMetadata;
    }

    @SuppressWarnings("unchecked")
    protected void injectTestSubjectMocks() throws IllegalAccessException {

        Set<Field> testSubjectFields = Whitebox.getFieldsAnnotatedWith(testInstance, TestSubject.class);
        for (Field testSubjectField : testSubjectFields) {
            Object testSubject = testSubjectField.get(testInstance);
            if (testSubject == null) {
                throw new NullPointerException("Have you forgotten to instantiate " + testSubjectField.getName() + "?");
            }
            injectTestSubjectFields(testSubject);

        }
    }

    protected void injectTestSubjectFields(Object testSubject) throws IllegalAccessException {
        Set<Field> targetFields = new HashSet<Field>(Whitebox.getAllInstanceFields(testSubject));
        targetFields = injectByName(targetFields, testSubject);
        injectByType(targetFields, testSubject);
    }

    void injectByType(Set<Field> targetFields, Object testSubject) throws IllegalAccessException {
        for (Field targetField : targetFields) {

            InjectionTarget target = new InjectionTarget(targetField);

            MockMetadata toAssign = findUniqueAssignable(target);

            if (toAssign == null) {
                continue;
            }

            target.inject(testSubject, toAssign);
        }
    }

    MockMetadata findUniqueAssignable(InjectionTarget target) {
        MockMetadata toAssign = null;
        for (MockMetadata mockMetadata : globalMetadata.getUnqualifiedInjections()) {
            if (target.accepts(mockMetadata)) {
                if (toAssign != null) {
                    throw new RuntimeException(String.format("At least two mocks can be assigned to '%s': %s and %s", target.getField(), toAssign.getMock(), mockMetadata.getMock()));
                }
                toAssign = mockMetadata;
            }
        }
        return toAssign;
    }

    Set<Field> injectByName(Set<Field> targetFields, Object targetObject) throws IllegalAccessException {
        Class<?> targetClass = targetObject.getClass();

        for (MockMetadata mockMetadata : globalMetadata.getQualifiedInjections()) {
            Field targetField = getFieldByName(targetClass, mockMetadata.getQualifier());

            if (targetField == null) {
                continue;
            }

            InjectionTarget target = new InjectionTarget(targetField);

            if (target.accepts(mockMetadata)) {
                target.inject(targetObject, mockMetadata);
                targetFields.remove(targetField);
            }
        }

        return targetFields;
    }

    private Field getFieldByName(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (SecurityException e) {
            return null;
        }
    }

    private static class InjectionTarget {

        private final Field field;

        public InjectionTarget(Field field) {

            this.field = field;
        }

        public Field getField() {
            return field;
        }

        public boolean accepts(MockMetadata mockMetadata) {
            return field.getType().isAssignableFrom(mockMetadata.getType());
        }

        public void inject(Object targetObject, MockMetadata mockMetadata) throws IllegalAccessException {
            field.setAccessible(true);

            Object value = field.get(targetObject);

            if (value == null) {
                field.set(targetObject, mockMetadata.getMock());
            }
        }

    }
}