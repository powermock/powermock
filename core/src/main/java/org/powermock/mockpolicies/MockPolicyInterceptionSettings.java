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

/**
 * Contains interception related settings. PowerMock uses the information stored
 * in this object to intercept method calls and field calls etc and specify a
 * return value or suppression.
 * <p>
 * Since mock policies can be chained previous policies can override behavior
 * of a previous policy. To avoid accidental overrides it's recommended
 * <i>add</i> behavior instead of <i>setting</i> behavior since the latter
 * overrides all previous configurations.
 */
public interface MockPolicyInterceptionSettings {
	/**
	 * Set which methods to suppress. Note that this overrides all previous
	 * configurations.
	 */
	void setMethodsToSuppress(Method[] methods);

	/**
	 * Add methods to suppress upon invocation.
	 */
	void addMethodsToSuppress(Method methodToSuppress, Method... additionalMethodsToSuppress);

	/**
	 * Add methods to suppress upon invocation.
	 */
	void addMethodsToSuppress(Method[] methods);

	/**
	 * Set the substitute return values. The substitute return values is a
	 * key-value map where each key is a method that should be intercepted and
	 * each value is the new return value for that method when it's intercepted.
	 * <p>
	 * Note that this overrides all previous configurations.
	 */
	void setSubtituteReturnValues(Map<Method, Object> substituteReturnValues);

	/**
	 * Add a method that should be intercepted and return another value (
	 * <code>returnObject</code>). The substitute return values is a key-value
	 * map where each key is a method that should be intercepted and each value
	 * is the new return value for that method when it's intercepted.
	 */
	void addSubtituteReturnValue(Method method, Object returnObject);

	/**
	 * Set specific fields that should be suppressed upon invocation. Note that
	 * this overrides all previous configurations.
	 */
	void setFieldsSuppress(Field[] fields);

	/**
	 * Add specific fields that should be suppressed upon invocation.
	 */
	void addFieldToSuppress(Field firstField, Field... additionalFields);

	/**
	 * Add specific fields that should be suppressed upon invocation.
	 */
	void addFieldToSuppress(Field[] fields);

	/**
	 * Set which field types that should be suppressed. Note that this overrides
	 * all previous configurations.
	 */
	void setFieldTypesToSuppress(String[] fieldTypes);

	/**
	 * Add field types that should be suppressed.
	 */
	void addFieldTypesToSuppress(String firstType, String... additionalFieldTypes);

	/**
	 * Add field types that should be suppressed.
	 */
	void addFieldTypesToSuppress(String[] fieldTypes);

	/**
	 * @return Which methods that should be suppressed/stubbed (i.e. return a
	 *         default value when invoked).
	 */
	Method[] getMethodsToSuppress();

	/**
	 * Get all substitute return values and also returns an unmodifiable map of
	 * all method-object pairs the were initialized.
	 */
	Map<Method, Object> getSubtituteReturnValues();

	/**
	 * @return Which fields should be suppressed (i.e. will be set to
	 *         <code>null</code> or other default values).
	 */
	Field[] getFieldsToSuppress();

	/**
	 * @return The fully-qualified names to the fields that should be
	 *         suppressed.
	 */
	String[] getFieldTypesToSuppress();
}
