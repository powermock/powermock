package org.powermock.tests.utils.impl;

import java.util.List;

/**
 * A test case entry consists of a test class and a list of chunk entries for
 * this test class.
 */
public class TestCaseEntry {

	private final List<TestChunk> testChunks;
	private final Class<?> testClass;

	public TestCaseEntry(Class<?> testClass, List<TestChunk> chunks) {
		this.testClass = testClass;
		this.testChunks = chunks;
	}

	public List<TestChunk> getTestChunks() {
		return testChunks;
	}

	public Class<?> getTestClass() {
		return testClass;
	}
}
