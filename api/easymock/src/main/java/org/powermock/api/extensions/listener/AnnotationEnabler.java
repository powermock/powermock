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
package org.powermock.api.extensions.listener;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createNiceMock;
import static org.powermock.api.easymock.PowerMock.createStrictMock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.powermock.api.easymock.annotation.MockNice;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.Mock;
import org.powermock.core.spi.support.AbstractPowerMockTestListenerBase;
import org.powermock.reflect.Whitebox;

/**
 * Before each test method all fields annotated with {@link Mock},
 * {@link MockNice} or {@link MockStrict} will have mock objects created for
 * them and injected to the fields.
 */
@SuppressWarnings("deprecation")
public class AnnotationEnabler extends AbstractPowerMockTestListenerBase {

	@Override
	public void beforeTestMethod(Object testInstance, Method method, Object[] arguments) throws Exception {
		injectDefaultMocks(testInstance);
		injectNiceMocks(testInstance);
		injectStrictMocks(testInstance);
	}

	protected void injectStrictMocks(Object testInstance) throws Exception {
		FieldInjector fieldInjector = new FieldInjector() {
			@Override
			public Object createMockInstance(Class<?> type, Method[] methods) {
				return createStrictMock(type, methods);
			}
		};
		fieldInjector.inject(testInstance, MockStrict.class);
	}

	protected void injectNiceMocks(Object testInstance) throws Exception {
		FieldInjector fieldInjector = new FieldInjector() {
			@Override
			public Object createMockInstance(Class<?> type, Method[] methods) {
				return createNiceMock(type, methods);
			}
		};
		fieldInjector.inject(testInstance, MockNice.class);
	}

	protected void injectDefaultMocks(Object testInstance) throws Exception {
		FieldInjector fieldInjector = new FieldInjector() {
			@Override
			public Object createMockInstance(Class<?> type, Method[] methods) {
				return createMock(type, methods);
			}
		};
		fieldInjector.inject(testInstance, org.powermock.api.easymock.annotation.Mock.class);
		fieldInjector.inject(testInstance, Mock.class);
	}

	protected abstract class FieldInjector {

		public void inject(Object testInstance, Class<? extends Annotation> annotation) throws Exception {
			Set<Field> fields = Whitebox.getFieldsAnnotatedWith(testInstance, annotation);
			for (Field field : fields) {
				final Class<?> type = field.getType();
				Annotation annotationInstance = field.getAnnotation(annotation);
				final String[] value = (String[]) Whitebox.invokeMethod(annotationInstance, "value");
				Method[] methods = null;
				if (value.length != 1 || !"".equals(value[0])) {
					methods = Whitebox.getMethods(type, value);
				}
				final Object createMock = createMockInstance(type, methods);
				field.set(testInstance, createMock);
			}
		}

		public abstract Object createMockInstance(final Class<?> type, final Method[] methods);
	}
}
