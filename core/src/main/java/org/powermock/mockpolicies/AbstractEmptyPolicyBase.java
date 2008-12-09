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
package org.powermock.mockpolicies;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.powermock.core.spi.PowerMockPolicy;

/**
 * A base class for mock policies that provides empty implementations of all
 * methods defined in the {@link PowerMockPolicy} interface so extending this
 * class can implemented only the required methods.
 */
public abstract class AbstractEmptyPolicyBase implements PowerMockPolicy {
	/**
	 * Provides an empty implementation
	 */
	public String[] getFieldTypesToSuppress() {
		return null;
	}

	/**
	 * Provides an empty implementation
	 */
	public Field[] getFieldsSuppress() {
		return null;
	}

	/**
	 * Provides an empty implementation
	 */
	public Method[] getMethodsToSuppress() {
		return null;
	}

	/**
	 * Provides an empty implementation
	 */
	public String[] getFullyQualifiedNamesOfClassesToLoadByMockClassloader() {
		return null;
	}

	/**
	 * Provides an empty implementation
	 */
	public Map<Method, Object> getSubtituteReturnValues() {
		return null;
	}

	/**
	 * Provides an empty implementation
	 */
	public String[] getStaticInitializersToSuppress() {
		return null;
	}
}
