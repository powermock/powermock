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
package org.powermock.mockpolicies.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.powermock.mockpolicies.MockPolicyInterceptionSettings;

public class MockPolicyInterceptionSettingsImpl implements MockPolicyInterceptionSettings {
	private Set<Field> fieldsToSuppress;
	private Set<Method> methodsToSuppress;
	private Map<Method, Object> substituteReturnValues;
	private Set<String> fieldsTypesToSuppress;

	public MockPolicyInterceptionSettingsImpl() {
		fieldsToSuppress = new LinkedHashSet<Field>();
		methodsToSuppress = new LinkedHashSet<Method>();
		substituteReturnValues = new HashMap<Method, Object>();
		fieldsTypesToSuppress = new LinkedHashSet<String>();
	}

	public void addFieldTypesToSuppress(String firstType, String... additionalFieldTypes) {
		fieldsTypesToSuppress.add(firstType);
		addFieldTypesToSuppress(additionalFieldTypes);
	}

	public void addFieldTypesToSuppress(String[] fieldTypes) {
		for (String fieldType : fieldTypes) {
			fieldsTypesToSuppress.add(fieldType);
		}
	}

	public void setFieldTypesToSuppress(String[] fieldTypes) {
		fieldsTypesToSuppress.clear();
		addFieldTypesToSuppress(fieldTypes);
	}

	public Field[] getFieldsToSuppress() {
		return fieldsToSuppress.toArray(new Field[fieldsToSuppress.size()]);
	}

	public Method[] getMethodsToSuppress() {
		return methodsToSuppress.toArray(new Method[methodsToSuppress.size()]);
	}

	public Map<Method, Object> getStubbedMethods() {
		return Collections.unmodifiableMap(substituteReturnValues);
	}

	public void addFieldToSuppress(Field firstField, Field... fields) {
		fieldsToSuppress.add(firstField);
		addFieldToSuppress(fields);
	}

	public void addFieldToSuppress(Field[] fields) {
		for (Field field : fields) {
			fieldsToSuppress.add(field);
		}
	}

	public void addMethodsToSuppress(Method methodToSuppress, Method... additionalMethodsToSuppress) {
		methodsToSuppress.add(methodToSuppress);
		addMethodsToSuppress(additionalMethodsToSuppress);
	}

	public void addMethodsToSuppress(Method[] methods) {
		for (Method method : methods) {
			methodsToSuppress.add(method);
		}
	}

	public void stubMethod(Method method, Object returnObject) {
		substituteReturnValues.put(method, returnObject);
	}

	public void setFieldsSuppress(Field[] fields) {
		fieldsToSuppress.clear();
		addFieldToSuppress(fields);
	}

	public void setMethodsToSuppress(Method[] methods) {
		methodsToSuppress.clear();
		addMethodsToSuppress(methods);
	}

	public void setMethodsToStub(Map<Method, Object> substituteReturnValues) {
		this.substituteReturnValues = substituteReturnValues;
	}

	public String[] getFieldTypesToSuppress() {
		return fieldsTypesToSuppress.toArray(new String[fieldsTypesToSuppress.size()]);
	}
}
