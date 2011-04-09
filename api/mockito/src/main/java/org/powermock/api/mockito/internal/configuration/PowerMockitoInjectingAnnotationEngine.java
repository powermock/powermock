/*
 * Copyright 2010 the original author or authors.
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
package org.powermock.api.mockito.internal.configuration;

import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.configuration.InjectingAnnotationEngine;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;

/**
 * The same as {@link InjectingAnnotationEngine} with the exception that it
 * doesn't create/injects mocks annotated with the standard annotations such as
 * {@link Mock}.
 */
public class PowerMockitoInjectingAnnotationEngine extends InjectingAnnotationEngine {

	@SuppressWarnings("deprecation")
	@Override
	public void process(Class<?> context, Object testClass) {
		// this will create @Spies:
		new PowerMockitoSpyAnnotationEngine().process(context, testClass);

		// this injects mocks
		Field[] fields = context.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(InjectMocks.class)) {
				try {
					Whitebox.invokeMethod(this, "assertNoAnnotations", field, new Class<?>[] { Mock.class, org.mockito.MockitoAnnotations.Mock.class,
							Captor.class });
				} catch (RuntimeException e) {
					throw e;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				injectMocks(testClass);
			}
		}
	}
}
