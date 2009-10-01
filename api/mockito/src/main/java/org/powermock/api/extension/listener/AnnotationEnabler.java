/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.api.extension.listener;

import static org.powermock.api.mockito.PowerMockito.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.mockito.MockitoAnnotations.Mock;
import org.powermock.core.spi.listener.AnnotationEnablerListener;
import org.powermock.core.spi.support.AbstractPowerMockTestListenerBase;
import org.powermock.reflect.Whitebox;

/**
 * Before each test method all fields annotated with {@link Mock},
 * {@link org.mockito.Mock} or {@link Mock} have mock objects created for them
 * and injected to the fields.
 * <p>
 * It will only inject to fields that haven't been set before (i.e that are
 * <code>null</code>).
 */
@SuppressWarnings("deprecation")
public class AnnotationEnabler extends AbstractPowerMockTestListenerBase implements AnnotationEnablerListener {

    @Override
    public void beforeTestMethod(Object testInstance, Method method, Object[] arguments) throws Exception {
        Set<Field> fields = Whitebox.getFieldsAnnotatedWith(testInstance, getMockAnnotations());
        for (Field field : fields) {
            if (field.get(testInstance) != null) {
                continue;
            }
            final Class<?> type = field.getType();
            if (field.isAnnotationPresent(org.powermock.core.classloader.annotations.Mock.class)) {
                org.powermock.core.classloader.annotations.Mock annotation = field
                        .getAnnotation(org.powermock.core.classloader.annotations.Mock.class);
                final String[] value = annotation.value();
                if (value.length != 1 || !"".equals(value[0])) {
                    System.err
                            .println("PowerMockito deprecation: Use PowerMockito.spy(..) for partial mocking instead. A standard mock will be created instead.");
                }
            }
            field.set(testInstance, mock(type));
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Annotation>[] getMockAnnotations() {
        return new Class[] { org.mockito.Mock.class, Mock.class, org.powermock.core.classloader.annotations.Mock.class };
    }
}
