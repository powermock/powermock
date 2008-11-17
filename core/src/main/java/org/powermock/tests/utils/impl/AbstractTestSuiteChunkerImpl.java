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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.TestSuiteChunker;

/**
 * Abstract base class for test suite chunking, i.e. a suite is chunked into
 * several smaller pieces which are ran with different classloaders. A chunk is
 * defined by the {@link PrepareForTest} annotation. This to make sure that you
 * can byte-code manipulate classes in tests without impacting on other tests.
 * 
 * @author Johan Haleby
 */
public abstract class AbstractTestSuiteChunkerImpl<T> implements TestSuiteChunker {
	protected static final int NOT_INITIALIZED = -1;

	private static final int INTERNAL_INDEX_NOT_FOUND = NOT_INITIALIZED;

	protected final TestClassesExtractor prepareForTestExtractor = new PrepareForTestExtractorImpl();

	protected final TestClassesExtractor suppressionExtractor = new StaticConstructorSuppressExtractorImpl();

	/*
	 * The classes listed in this set has been chunked and its delegates has
	 * been created.
	 */
	private final Set<Class<?>> delegatesCreatedForTheseClasses = new LinkedHashSet<Class<?>>();

	// A list of junit delegates.
	protected final List<T> delegates = new LinkedList<T>();

	/*
	 * Maps the list of test indexes that is assigned to a specific test suite
	 * index.
	 */
	protected final Map<Integer, List<Integer>> testAtDelegateMapper = new HashMap<Integer, List<Integer>>();

	private int currentTestIndex = NOT_INITIALIZED;

	/*
	 * Maps between a specific class and a map of test methods loaded by a
	 * specific mock class loader.
	 */
	private final Map<Class<?>, Map<ClassLoader, List<Method>>> internalSuites;

	protected volatile int testCount = NOT_INITIALIZED;

	protected AbstractTestSuiteChunkerImpl(Class<?> testClass) throws Exception {
		this(new Class[] { testClass });
	}

	protected AbstractTestSuiteChunkerImpl(Class<?>... testClasses) throws Exception {
		internalSuites = new ConcurrentHashMap<Class<?>, Map<ClassLoader, List<Method>>>();
		for (Class<?> clazz : testClasses) {
			chunkClass(clazz);
		}
	}

	protected void chunkClass(Class<?> testClass) throws Exception {
		ClassLoader defaultMockLoader = null;
		final String[] ignorePackages = getIgnorePackages(testClass);
		if (testClass.isAnnotationPresent(PrepareEverythingForTest.class)) {
			defaultMockLoader = createNewClassloader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES }, ignorePackages);
		} else {
			final String[] prepareForTestClasses = prepareForTestExtractor.getTestClasses(testClass);
			final String[] suppressStaticClasses = suppressionExtractor.getTestClasses(testClass);
			defaultMockLoader = createNewClassloader(prepareForTestClasses, suppressStaticClasses, ignorePackages);
		}
		List<Method> currentClassloaderMethods = new LinkedList<Method>();
		// Put the first suite in the map of internal suites.
		Map<ClassLoader, List<Method>> suites = new ConcurrentHashMap<ClassLoader, List<Method>>();
		suites.put(defaultMockLoader, currentClassloaderMethods);
		internalSuites.put(testClass, suites);
		initEntries(testClass, currentClassloaderMethods, internalSuites);
		/*
		 * If we don't have any test that should be executed by the default
		 * class loader remove it to avoid duplicate test print outs.
		 */
		if (currentClassloaderMethods.isEmpty()) {
			// We ignore this since other tests fails because of this, we need
			// to find a better solution.
			// internalSuites.get(testClass).remove(defaultMockLoader);
		}
	}

	private String[] getIgnorePackages(Class<?> testClass) {
		PowerMockIgnore annotation = testClass.getAnnotation(PowerMockIgnore.class);
		if (annotation != null) {
			return annotation.value();
		}
		return new String[0];
	}

	private ClassLoader createNewClassloader(final String[] prepareForTestClasses, final String[] suppressStaticClasses, final String[] ignorePackages) {
		return createNewClassloader(getClassesToBeModified(prepareForTestClasses, suppressStaticClasses), ignorePackages);
	}

	private String[] getClassesToBeModified(String[] prepareForTestClasses, String[] suppressStaticClasses) {
		if (prepareForTestClasses == null) {
			prepareForTestClasses = new String[0];
		}
		if (suppressStaticClasses == null) {
			suppressStaticClasses = new String[0];
		}

		final int prepareForTestLength = prepareForTestClasses.length;
		final int suppressLength = suppressStaticClasses.length;
		final int allClassesLength = prepareForTestLength + suppressLength;
		String[] classesToLoadedByMockClassLoader = new String[allClassesLength];
		if (allClassesLength > 0) {
			System.arraycopy(prepareForTestClasses, 0, classesToLoadedByMockClassLoader, 0, prepareForTestLength);
			System.arraycopy(suppressStaticClasses, 0, classesToLoadedByMockClassLoader, prepareForTestLength, suppressLength);
		}
		return classesToLoadedByMockClassLoader;
	}

	public ClassLoader createNewClassloader(final String[] classes, final String[] packagesToIgnore) {
		ClassLoader mockLoader = null;
		if (classes == null || classes.length == 0) {
			mockLoader = Thread.currentThread().getContextClassLoader();
		} else {
			List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
			final MainMockTransformer mainMockTransformer = new MainMockTransformer();
			mockTransformerChain.add(mainMockTransformer);

			mockLoader = new MockClassLoader(classes, packagesToIgnore);
			((MockClassLoader) mockLoader).setMockTransformerChain(mockTransformerChain);
		}
		return mockLoader;
	}

	/**
	 * {@inheritDoc}
	 */
	public void createTestDelegators(Class<?> testClass, Set<Entry<ClassLoader, List<Method>>> entrySet) throws Exception {
		for (Entry<ClassLoader, List<Method>> entry : entrySet) {
			ClassLoader classLoader = entry.getKey();
			List<Method> methodsToTest = entry.getValue();
			T runnerDelegator = createDelegatorFromClassloader(classLoader, testClass, methodsToTest);
			delegates.add(runnerDelegator);
		}
		delegatesCreatedForTheseClasses.add(testClass);
	}

	protected abstract T createDelegatorFromClassloader(ClassLoader classLoader, Class<?> testClass, final List<Method> methodsToTest)
			throws Exception;

	private void initEntries(Class<?> testClass, List<Method> currentClassloaderMethods, Map<Class<?>, Map<ClassLoader, List<Method>>> testSuites)
			throws Exception {
		Method[] allMethods = testClass.getMethods();
		for (Method method : allMethods) {
			if (shouldExecuteTestForMethod(testClass, method)) {
				currentTestIndex++;
				if (hasChunkAnnotation(method)) {
					LinkedList<Method> suiteMethods = new LinkedList<Method>();
					suiteMethods.add(method);
					final Map<ClassLoader, List<Method>> suitesForTestClass = testSuites.get(testClass);
					final String[] staticSuppressionClasses = getStaticSuppressionClasses(testClass, method);
					ClassLoader mockClassloader = null;
					if (method.isAnnotationPresent(PrepareEverythingForTest.class)) {
						mockClassloader = createNewClassloader(new String[] { MockClassLoader.MODIFY_ALL_CLASSES }, getIgnorePackages(testClass));
					} else {
						mockClassloader = createNewClassloader(prepareForTestExtractor.getTestClasses(method), staticSuppressionClasses,
								getIgnorePackages(testClass));
					}
					if (suitesForTestClass == null) {
						final Map<ClassLoader, List<Method>> newSuite = new ConcurrentHashMap<ClassLoader, List<Method>>();
						addToTestSuite(testClass, testSuites, suiteMethods, mockClassloader, newSuite);
					} else {
						addToTestSuite(testClass, testSuites, suiteMethods, mockClassloader, suitesForTestClass);
					}

				} else {
					currentClassloaderMethods.add(method);
					final int currentDelegateIndex = testSuites.size() - 1;
					/*
					 * Add this test index to the main junit runner delegator.
					 */
					List<Integer> testList = testAtDelegateMapper.get(currentDelegateIndex);
					if (testList == null) {
						testList = new LinkedList<Integer>();
						testAtDelegateMapper.put(currentDelegateIndex, testList);
					}

					testList.add(currentTestIndex);
				}
			}
		}
	}

	private boolean hasChunkAnnotation(Method method) {
		return method.isAnnotationPresent(PrepareForTest.class) || method.isAnnotationPresent(SuppressStaticInitializationFor.class)
				|| method.isAnnotationPresent(PrepareOnlyThisForTest.class) || method.isAnnotationPresent(PrepareEverythingForTest.class);
	}

	private String[] getStaticSuppressionClasses(Class<?> testClass, Method method) {
		String[] testClasses = null;
		if (method.isAnnotationPresent(SuppressStaticInitializationFor.class)) {
			testClasses = suppressionExtractor.getTestClasses(method);
		} else {
			testClasses = suppressionExtractor.getTestClasses(testClass);
		}
		return testClasses;
	}

	private void addToTestSuite(Class<?> testClass, Map<Class<?>, Map<ClassLoader, List<Method>>> testSuites, LinkedList<Method> suiteMethods,
			final ClassLoader classLoader, final Map<ClassLoader, List<Method>> suite) {
		suite.put(classLoader, suiteMethods);
		testSuites.put(testClass, suite);
		final List<Integer> testIndexesForThisClassloader = new LinkedList<Integer>();
		testIndexesForThisClassloader.add(currentTestIndex);
		testAtDelegateMapper.put(testSuites.size(), testIndexesForThisClassloader);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTestClassToSuite(Class<?> clazz) throws Exception {
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

	public Set<Entry<ClassLoader, List<Method>>> getAllChunkEntries() {
		Set<Entry<Class<?>, Map<ClassLoader, List<Method>>>> entrySet = internalSuites.entrySet();
		Set<Entry<ClassLoader, List<Method>>> set = new LinkedHashSet<Entry<ClassLoader, List<Method>>>();
		for (Entry<Class<?>, Map<ClassLoader, List<Method>>> entry : entrySet) {
			Set<Entry<ClassLoader, List<Method>>> entrySet2 = entry.getValue().entrySet();
			for (Entry<ClassLoader, List<Method>> entry2 : entrySet2) {
				set.add(entry2);
			}
		}
		return set;
	}

	/**
	 * Get the internal test index for a junit runner delegate based on the
	 * "real" original test index. For example, the test may need to run a
	 * single test, for example the test with index 3. However since PowerMock
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
		Set<Entry<Integer, List<Integer>>> delegatorEntrySet = testAtDelegateMapper.entrySet();
		for (Entry<Integer, List<Integer>> entry : delegatorEntrySet) {
			final List<Integer> testIndexesForThisDelegate = entry.getValue();
			final int internalIndex = testIndexesForThisDelegate.indexOf(originalTestIndex);
			if (internalIndex != INTERNAL_INDEX_NOT_FOUND) {
				return internalIndex;
			}
		}
		return INTERNAL_INDEX_NOT_FOUND;
	}

	/**
	 * Get the junit runner delegate that handles the test at index
	 * <code>testIndex</code>. Throws a {@link RuntimeException} if a delegator
	 * is not found for the specific test index.
	 * 
	 * @param testIndex
	 *            The test index that a delegator should hold.
	 * @return The index for of the junit runner delegate as seen by JTestRack.
	 */
	public int getDelegatorIndex(int testIndex) {
		int delegatorIndex = -1;
		Set<Entry<Integer, List<Integer>>> entrySet = testAtDelegateMapper.entrySet();
		for (Entry<Integer, List<Integer>> entry : entrySet) {
			// If the delegator contains the test case, return the index of the
			// delegator.
			if (entry.getValue().contains(testIndex)) {
				delegatorIndex = entry.getKey();
				break;
			}
		}

		if (delegatorIndex == -1) {
			throw new RuntimeException("Internal error: Failed to find the delgator index.");
		}
		return delegatorIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Entry<ClassLoader, List<Method>>> getChunkEntries(Class<?> testClass) {
		final Map<ClassLoader, List<Method>> map = internalSuites.get(testClass);
		return map.entrySet();
	}

	/**
	 * Initialize test state.
	 */
	protected void initializeTestState() {
		// getStaticSuppressionClasses(getClass());
	}
}
