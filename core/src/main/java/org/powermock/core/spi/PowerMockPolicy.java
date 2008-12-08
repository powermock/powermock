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
package org.powermock.core.spi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * This interface can be implemented to create policies for certain frameworks
 * to make it easier for users to test their code in isolation from these
 * frameworks. A mock policy implementation can for example suppress some
 * methods, suppress static initializers or intercept method calls and change
 * their return value (for example to return a mock object).
 */
public interface PowerMockPolicy {

	String[] getStaticInitializersToSuppress();

	Method[] getMethodsToSuppress();

	Map<Method, Object> getSubtituteReturnValues();

	String[] getFieldTypesToSuppress();

	Field[] getFieldsSuppress();

	String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader();
}
