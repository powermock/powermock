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

import java.util.List;

import org.powermock.tests.utils.TestChunk;

/**
 * A test case entry consists of a test class and a list of test chunks that
 * should be executed for this entry.
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
