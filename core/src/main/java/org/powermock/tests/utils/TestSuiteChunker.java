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
package org.powermock.tests.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * An interface that should be implemented by classes that performs test suite
 * chunking. Test suite chunking may be performed because certain classes may
 * need to be byte-code manipulated in tests without impacting on other tests.
 * 
 * @author Johan Haleby
 */
public interface TestSuiteChunker {

	/**
	 * Create the test delegators needed for a whole class.
	 */
	public void createTestDelegators(Class<?> testClass, Set<Entry<ClassLoader, List<Method>>> entrySet) throws Exception;

	/**
	 * Get the number of chunks defined in this suite.
	 * 
	 * @return The number of chunks defined in the correct suite.
	 */
	public int getChunkSize();

	/**
	 * Get all chunk entries.
	 * 
	 * 
	 * @return An set of entries that contains a list of methods contained in
	 *         the chunk and the class loader that loaded these methods.
	 */
	public Set<Entry<ClassLoader, List<Method>>> getAllChunkEntries();

	/**
	 * Get all chunk entries for a specific class.
	 * 
	 * @param testClass
	 *            The class whose chunk entries to get.
	 * @return An set of entries that contains a list of methods contained in
	 *         the chunk for the specific test class and the class loader that
	 *         loaded these methods.
	 */
	public Set<Entry<ClassLoader, List<Method>>> getChunkEntries(Class<?> testClass);

	/**
	 * Should reflect whether or not this method is eligible for testing.
	 * 
	 * @param testClass
	 *            The class that defines the method.
	 * @param potentialTestMethod
	 *            The method to inspect whether it should be executed in the
	 *            test suite or not.
	 * 
	 * @return <code>true</code> if the method is a test method and should be
	 *         executed, <code>false</code> otherwise.
	 * 
	 */
	public boolean shouldExecuteTestForMethod(Class<?> testClass, Method potentialTestMethod);

	/**
	 * Add a class to the test suite. Methods in this class will be checked
	 * according to {@link #shouldExecuteTestForMethod(Class, Method)} to see if
	 * it should be executed.
	 * 
	 * @param clazz
	 *            The class that should contain test cases.
	 * @throws Exception
	 *             If something unexpected goes wrong.
	 */
	public void addTestClassToSuite(Class<?> clazz) throws Exception;

	/**
	 * Create a new class loader and load <code>classes</code> from this
	 * classloader.
	 * 
	 * @param classes
	 *            An array of the fully qualified name of the classes to modify.
	 */
	public ClassLoader createNewClassloader(String[] classes);

	/**
	 * Get the number of total tests defined in the suite (the sum of all tests
	 * defined in all chunks for this suite).
	 * 
	 * @return The number of tests in this suite.
	 */
	public int getTestCount();
}
