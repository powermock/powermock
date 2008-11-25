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
package org.powermock.modules.junit3;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.powermock.modules.junit3.internal.JUnit3TestSuiteChunker;
import org.powermock.modules.junit3.internal.impl.JUnit3TestSuiteChunkerImpl;

/**
 * A custom JUnit 3 Suite that must be used to run JTestRack tests under JUnit3.
 * 
 * @author Johan Haleby
 */
public class PowerMockSuite extends TestSuite {

	private JUnit3TestSuiteChunker testChunker;

	public PowerMockSuite(String name, Class<? extends TestCase>... testCases) throws Exception {
		if (name != null) {
			setName(name);
		}
		testChunker = new JUnit3TestSuiteChunkerImpl(testCases);
	}

	public PowerMockSuite(Class<? extends TestCase>... testCases) throws Exception {
		this(null, testCases);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(TestResult result) {
		testChunker.run(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTest(Test test) {
		try {
			testChunker.addTest(test);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void runTest(Test test, TestResult result) {
		testChunker.runTest(test, result);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addTestSuite(Class testClass) {
		try {
			testChunker.addTestSuite(testClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Test testAt(int index) {
		return testChunker.testAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int testCount() {
		return testChunker.getTestCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration tests() {
		return testChunker.tests();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countTestCases() {
		return testChunker.countTestCases();
	}
}
