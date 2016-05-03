/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.testlisteners;

import org.powermock.core.spi.support.AbstractPowerMockTestListenerBase;
import org.powermock.core.spi.testresult.TestMethodResult;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * A test listener that automatically set all instance fields to their default
 * values after each test method. E.g. an object field is set to
 * {@code null}, an {@code int} field is set to 0 and so on.
 */
public class FieldDefaulter extends AbstractPowerMockTestListenerBase {

	@Override
	public void afterTestMethod(Object testInstance, Method method, Object[] arguments, TestMethodResult testResult) throws Exception {
		Set<Field> allFields = Whitebox.getAllInstanceFields(testInstance);
		for (Field field : allFields) {
			field.set(testInstance, TypeUtils.getDefaultValue(field.getType()));
		}
	}
}
