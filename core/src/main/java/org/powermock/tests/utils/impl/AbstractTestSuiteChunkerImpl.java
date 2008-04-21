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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.tests.utils.PrepareForTestExtractor;
import org.powermock.tests.utils.StaticConstructorSuppressionExtractor;
import org.powermock.tests.utils.TestSuiteChunker;

/**
 * Abstract base class for test suite chunking, i.e. a suite is chunked into
 * several smaller pieces which are ran with different classloaders. A chunk is
 * defined by the {@link PrepareForTest} annotation. This to make sure that you
 * can byte-code manipulate classes in tests without impacting on other tests.
 * 
 * @author Johan Haleby
 */
public abstract class AbstractTestSuiteChunkerImpl<T> implements
		TestSuiteChunker {
	protected static final int NOT_INITIALIZED = -1;

	private static final int INTERNAL_INDEX_NOT_FOUND = NOT_INITIALIZED;

	protected final PrepareForTestExtractor prepareForTestExtractor = new PrepareForTestExtractorImpl();

	private final StaticConstructorSuppressionExtractor suppressionExtractor = new StaticConstructorSuppressImpl();

	/*
	 * The classes listed in this set has been chunked and its delegates has
	 * been created.
	 */
	private final Set<Class<?>> delegatesCreatedForTheseClasses = new HashSet<Class<?>>();

	// A list of junit delegates.
	protected final List<T> delegates = new LinkedList<T>();

	/*
	 * Maps the list of test indexes that is assigned to a specific test suite
	 * index.
	 */
	protected final Map<Integer, List<Integer>> testAtDelegateMapper = new ConcurrentHashMap<Integer, List<Integer>>();

	private int currentTestIndex = NOT_INITIALIZED;

	/*
	 * Maps between a specific class and a map of test methods loaded by a
	 * specific mock class loader.
	 * 
	 */
	private final Map<Class<?>, Map<MockClassLoader, List<Method>>> internalSuites;

	protected volatile int testCount = NOT_INITIALIZED;

	protected AbstractTestSuiteChunkerImpl(Class<?> testClass) {
		this(new Class[] { testClass });
	}

	protected AbstractTestSuiteChunkerImpl(Class<?>... testClasses) {
		internalSuites = new ConcurrentHashMap<Class<?>, Map<MockClassLoader, List<Method>>>();
		for (Class<?> clazz : testClasses) {
			chunkClass(clazz);
		}
	}

	protected void chunkClass(Class<?> testClass) {
		final String[] prepareForTestClasses = prepareForTestExtractor
				.getClassLevelElements(testClass);
		final String[] suppressStaticClasses = suppressionExtractor
				.getClassLevelElements(testClass);
		String[] classesToLoadedByMockClassLoader = new String[prepareForTestClasses.length
				+ suppressStaticClasses.length];
		System.arraycopy(prepareForTestClasses, 0,
				classesToLoadedByMockClassLoader, 0,
				prepareForTestClasses.length);
		System.arraycopy(suppressStaticClasses, 0,
				classesToLoadedByMockClassLoader, prepareForTestClasses.length,
				suppressStaticClasses.length);
		MockClassLoader defaultMockLoader = createNewMockClassloader(classesToLoadedByMockClassLoader);
		List<Method> currentClassloaderMethods = new LinkedList<Method>();
		// Put the first suite in the map of internal suites.
		Map<MockClassLoader, List<Method>> suites = new ConcurrentHashMap<MockClassLoader, List<Method>>();
		suites.put(defaultMockLoader, currentClassloaderMethods);
		internalSuites.put(testClass, suites);
		initEntries(testClass, currentClassloaderMethods, internalSuites);
	}

	public MockClassLoader createNewMockClassloader(String[] classes) {
		List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
		mockTransformerChain.add(new MainMockTransformer());

		MockClassLoader mockLoader = new MockClassLoader(classes);
		mockLoader.setMockTransformerChain(mockTransformerChain);
		return mockLoader;
	}

	/**
	 * {@inheritDoc}
	 */
	public void createTestDelegators(Class<?> testClass,
			Set<Entry<MockClassLoader, List<Method>>> entrySet)
			throws Exception {
		for (Entry<MockClassLoader, List<Method>> entry : entrySet) {
			MockClassLoader mockClassLoader = entry.getKey();
			List<Method> methodsToTest = entry.getValue();
			T runnerDelegator = createDelegatorFromClassloader(mockClassLoader,
					testClass, methodsToTest);
			delegates.add(runnerDelegator);
		}
		delegatesCreatedForTheseClasses.add(testClass);
	}

	protected abstract T createDelegatorFromClassloader(
			MockClassLoader classLoader, Class<?> testClass,
			final List<Method> methodsToTest) throws Exception;

	private void initEntries(Class<?> testClass,
			List<Method> currentClassloaderMethods,
			Map<Class<?>, Map<MockClassLoader, List<Method>>> testSuites) {
		Method[] allMethods = testClass.getMethods();
		for (Method method : allMethods) {
			if (shouldExecuteTestForMethod(method)) {
				currentTestIndex++;
				if (method.isAnnotationPresent(PrepareForTest.class)) {
					LinkedList<Method> suiteMethods = new LinkedList<Method>();
					suiteMethods.add(method);
					final Map<MockClassLoader, List<Method>> suitesForTestClass = testSuites
							.get(testClass);
					final MockClassLoader mockClassloader = createNewMockClassloader(prepareForTestExtractor
							.getMethodLevelElements(method));
					if (suitesForTestClass == null) {
						final Map<MockClassLoader, List<Method>> newSuite = new ConcurrentHashMap<MockClassLoader, List<Method>>();
						addToTestSuite(testClass, testSuites, suiteMethods,
								mockClassloader, newSuite);
					} else {
						addToTestSuite(testClass, testSuites, suiteMethods,
								mockClassloader, suitesForTestClass);
					}

				} else {
					currentClassloaderMethods.add(method);
					final int currentDelegateIndex = testSuites.size() - 1;
					/*
					 * Add this test index to the main junit runner delegator.
					 */
					List<Integer> testList = testAtDelegateMapper
							.get(currentDelegateIndex);
					if (testList == null) {
						testList = new LinkedList<Integer>();
						testAtDelegateMapper
								.put(currentDelegateIndex, testList);
					}

					testList.add(currentTestIndex);
				}
			}
		}
	}

	private void addToTestSuite(Class<?> testClass,
			Map<Class<?>, Map<MockClassLoader, List<Method>>> testSuites,
			LinkedList<Method> suiteMethods,
			final MockClassLoader mockClassloader,
			final Map<MockClassLoader, List<Method>> suite) {
		suite.put(mockClassloader, suiteMethods);
		testSuites.put(testClass, suite);
		final List<Integer> testIndexesForThisClassloader = new LinkedList<Integer>();
		testIndexesForThisClassloader.add(currentTestIndex);
		testAtDelegateMapper.put(testSuites.size(),
				testIndexesForThisClassloader);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTestClassToSuite(Class<?> clazz) {
		chunkClass(clazz);
		if (!delegatesCreatedForTheseClasses.contains(clazz)) {
			try {
				createTestDelegators(clazz, getChunkEntries(clazz));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getChunkSize() {
		return getAllChunkEntries().size();
	}

	public Set<Entry<MockClassLoader, List<Method>>> getAllChunkEntries() {
		Set<Entry<Class<?>, Map<MockClassLoader, List<Method>>>> entrySet = internalSuites
				.entrySet();
		Set<Entry<MockClassLoader, List<Method>>> set = new HashSet<Entry<MockClassLoader, List<Method>>>();
		for (Entry<Class<?>, Map<MockClassLoader, List<Method>>> entry : entrySet) {
			Set<Entry<MockClassLoader, List<Method>>> entrySet2 = entry
					.getValue().entrySet();
			for (Entry<MockClassLoader, List<Method>> entry2 : entrySet2) {
				set.add(entry2);
			}
		}
		return set;
	}

	/**
	 * Get the internal test index for a junit runner delegate based on the
	 * "real" original test index. For example, the test may need to run a
	 * single test, for example the test with index 3. However since JTestRack
	 * may have chunked the test suite to use many classloaders and junit
	 * delegators the index (3) must be mapped to an internal representation for
	 * the specific junit runner delegate. This is what this method does. I.e.
	 * it will iterate through all junit runner delegates and see if they
	 * contain the test with index 3, in the internal index of this test
	 * delegator is returned.
	 * 
	 * @param originalTestIndex
	 *            The original test index as seen by the test runner.
	 * @return The internal test index as seen by JTestRack or <code>-1</code>
	 *         if no index was found.
	 * 
	 */
	public int getInternalTestIndex(int originalTestIndex) {
		Set<Entry<Integer, List<Integer>>> delegatorEntrySet = testAtDelegateMapper
				.entrySet();
		for (Entry<Integer, List<Integer>> entry : delegatorEntrySet) {
			final List<Integer> testIndexesForThisDelegate = entry.getValue();
			final int internalIndex = testIndexesForThisDelegate
					.indexOf(originalTestIndex);
			if (internalIndex != INTERNAL_INDEX_NOT_FOUND) {
				return internalIndex;
			}
		}
		return INTERNAL_INDEX_NOT_FOUND;
	}

	/**
	 * Get the junit runner delegate that handles the test at index
	 * <code>testIndex</code>. Throws a {@link RuntimeException} if a
	 * delegator is not found for the specific test index.
	 * 
	 * @param testIndex
	 *            The test index that a delegator should hold.
	 * @return The index for of the junit runner delegate as seen by JTestRack.
	 */
	public int getDelegatorIndex(int testIndex) {
		int delegatorIndex = -1;
		Set<Entry<Integer, List<Integer>>> entrySet = testAtDelegateMapper
				.entrySet();
		for (Entry<Integer, List<Integer>> entry : entrySet) {
			// If the delegator contains the test case, return the index of the
			// delegator.
			if (entry.getValue().contains(testIndex)) {
				delegatorIndex = entry.getKey();
				break;
			}
		}

		if (delegatorIndex == -1) {
			throw new RuntimeException(
					"Internal error: Failed to find the delgator index.");
		}
		return delegatorIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Entry<MockClassLoader, List<Method>>> getChunkEntries(
			Class<?> testClass) {
		final Map<MockClassLoader, List<Method>> map = internalSuites
				.get(testClass);
		return map.entrySet();
	}
}
