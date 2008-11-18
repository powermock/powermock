package org.powermock.tests.utils.impl;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A test chunk consists of a test class and a map
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
