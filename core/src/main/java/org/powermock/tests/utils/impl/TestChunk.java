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
package org.powermock.tests.utils.impl;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A test chunk consists of a list of methods that should be executed by a
 * particular classloader.
 */
public class TestChunk {

	private final ClassLoader classLoader;
	private final List<Method> testMethodsToBeExecutedByThisClassloader;

	public TestChunk(ClassLoader classLoader, List<Method> testMethodsToBeExecutedByThisClassloader) {
		this.classLoader = classLoader;
		this.testMethodsToBeExecutedByThisClassloader = testMethodsToBeExecutedByThisClassloader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public List<Method> getTestMethodsToBeExecutedByThisClassloader() {
		return testMethodsToBeExecutedByThisClassloader;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Classloader = ").append(classLoader).append("\n");
		sb.append("Methods:\n");
		for (Method method : testMethodsToBeExecutedByThisClassloader) {
			sb.append("  ").append(method).append("\n");
		}
		return sb.toString();
	}
}
