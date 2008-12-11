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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.TestSuiteChunker;

/**
 * Abstract base class for test suite chunking, i.e. a suite is chunked into
 * several smaller pieces which are ran with different classloaders. A chunk is
 * defined by the {@link PrepareForTest} annotation. This to make sure that you
 * can byte-code manipulate classes in tests without impacting on other tests.
 * 
 */
public abstract class AbstractTestSuiteChunkerImpl<T> implements TestSuiteChunker {
	protected static final int NOT_INITIALIZED = -1;

	private static final int INTERNAL_INDEX_NOT_FOUND = NOT_INITIALIZED;

	protected final TestClassesExtractor prepareForTestExtractor = new PrepareForTestExtractorImpl();

	protected final TestClassesExtractor suppressionExtractor = new StaticConstructorSuppressExtractorImpl();

	private final ArrayMerger arrayMerger = new ArrayMergerImpl();

	private final Class<?>[] testClasses;

	/*
	 * The classes listed in this set has been chunked and its delegates has
	 * been created.
	 */
	protected final Set<Class<?>> delegatesCreatedForTheseClasses = new LinkedHashSet<Class<?>>();

	// A list of junit delegates.
	protected final List<T> delegates = new LinkedList<T>();

	/*
	 * Maps the list of test indexes that is assigned to a specific test suite
	 * index.
	 */
	protected final LinkedHashMap<Integer, List<Integer>> testAtDelegateMapper = new LinkedHashMap<Integer, List<Integer>>();

	private int currentTestIndex = NOT_INITIALIZED;

	/*
	 * Maps between a specific class and a map of test methods loaded by a
	 * specific mock class loader.
	 */
	private final List<TestCaseEntry> internalSuites;

	protected volatile int testCount = NOT_INITIALIZED;

	protected AbstractTestSuiteChunkerImpl(Class<?> testClass) throws Exception {
		this(new Class[] { testClass });
	}

	protected AbstractTestSuiteChunkerImpl(Class<?>... testClasses) throws Exception {
		this.testClasses = testClasses;
		internalSuites = new LinkedList<TestCaseEntry>();
		for (Class<?> clazz : testClasses) {
			chunkClass(clazz);
		}
	}

	protected Object getPowerMockTestListenersLoadedByASpecificClassLoader(Class<?> clazz, ClassLoader classLoader) {
		try {
			final Class<?> powerMockTestListenerType = Class.forName(PowerMockTestListener.class.getName(), false, classLoader);
			Object testListeners = null;
			if (clazz.isAnnotationPresent(PowerMockListener.class)) {
				PowerMockListener annotation = clazz.getAnnotation(PowerMockListener.class);
				final Class<? extends PowerMockTestListener>[] powerMockTestListeners = annotation.value();
				if (powerMockTestListeners.length > 0) {
					testListeners = Array.newInstance(powerMockTestListenerType, powerMockTestListeners.length);
					for (int i = 0; i < powerMockTestListeners.length; i++) {
						final Class<?> listenerTypeLoadedByClassLoader = Class.forName(powerMockTestListeners[i].getName(), false, classLoader);
						Array.set(testListeners, i, Whitebox.newInstance(listenerTypeLoadedByClassLoader));
					}
				}
			} else {
				testListeners = Array.newInstance(powerMockTestListenerType, 0);
			}
			return testListeners;
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("PowerMock internal error: Failed to load class.", e);
		}
	}

	protected void chunkClass(final Class<?> testClass) throws Exception {
		ClassLoader defaultMockLoader = null;
		final String[] ignorePackages = getIgnorePackages(testClass);
		if (testClass.isAnnotationPresent(PrepareEverythingForTest.class)) {
			defaultMockLoader = createNewClassloader(testClass, new String[] { MockClassLoader.MODIFY_ALL_CLASSES }, ignorePackages);
		} else {
			final String[] prepareForTestClasses = prepareForTestExtractor.getTestClasses(testClass);
			final String[] suppressStaticClasses = suppressionExtractor.getTestClasses(testClass);
			defaultMockLoader = createNewClassloader(testClass, arrayMerger.mergeArrays(String.class, prepareForTestClasses, suppressStaticClasses),
					ignorePackages);
		}
		List<Method> currentClassloaderMethods = new LinkedList<Method>();
		// Put the first suite in the map of internal suites.
		TestChunk defaultTestChunk = new TestChunk(defaultMockLoader, currentClassloaderMethods);
		List<TestChunk> testChunks = new LinkedList<TestChunk>();
		testChunks.add(defaultTestChunk);
		internalSuites.add(new TestCaseEntry(testClass, testChunks));
		initEntries(internalSuites);
		/*
		 * If we don't have any test that should be executed by the default
		 * class loader remove it to avoid duplicate test print outs.
		 */
		if (currentClassloaderMethods.isEmpty()) {
			internalSuites.get(0).getTestChunks().remove(0);
		}
	}

	private String[] getIgnorePackages(Class<?> testClass) {
		PowerMockIgnore annotation = testClass.getAnnotation(PowerMockIgnore.class);
		if (annotation != null) {
			return annotation.value();
		}
		return new String[0];
	}

	public ClassLoader createNewClassloader(Class<?> testClass, final String[] classesToLoadByMockClassloader, final String[] packagesToIgnore) {
		ClassLoader mockLoader = null;
		if ((classesToLoadByMockClassloader == null || classesToLoadByMockClassloader.length == 0) && !hasMockPolicyProvidedClasses(testClass)) {
			mockLoader = Thread.currentThread().getContextClassLoader();
		} else {
			List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
			final MainMockTransformer mainMockTransformer = new MainMockTransformer();
			mockTransformerChain.add(mainMockTransformer);

			mockLoader = new MockClassLoader(classesToLoadByMockClassloader, packagesToIgnore);
			((MockClassLoader) mockLoader).setMockTransformerChain(mockTransformerChain);
			initializeMockPolicies(testClass, (MockClassLoader) mockLoader);
		}
		return mockLoader;
	}

	/**
	 * {@inheritDoc}
	 */
	public void createTestDelegators(Class<?> testClass, List<TestChunk> chunks) throws Exception {
		for (TestChunk chunk : chunks) {
			ClassLoader classLoader = chunk.getClassLoader();
			List<Method> methodsToTest = chunk.getTestMethodsToBeExecutedByThisClassloader();
			T runnerDelegator = createDelegatorFromClassloader(classLoader, testClass, methodsToTest);
			delegates.add(runnerDelegator);
		}
		delegatesCreatedForTheseClasses.add(testClass);
	}

	protected abstract T createDelegatorFromClassloader(ClassLoader classLoader, Class<?> testClass, final List<Method> methodsToTest)
			throws Exception;

	private void initEntries(List<TestCaseEntry> entries) throws Exception {
		for (TestCaseEntry testCaseEntry : entries) {
			final Class<?> testClass = testCaseEntry.getTestClass();
			Method[] allMethods = testClass.getMethods();
			for (Method method : allMethods) {
				if (shouldExecuteTestForMethod(testClass, method)) {
					currentTestIndex++;
					if (hasChunkAnnotation(method)) {
						LinkedList<Method> methodsInThisChunk = new LinkedList<Method>();
						methodsInThisChunk.add(method);
						final String[] staticSuppressionClasses = getStaticSuppressionClasses(testClass, method);
						ClassLoader mockClassloader = null;
						if (method.isAnnotationPresent(PrepareEverythingForTest.class)) {
							mockClassloader = createNewClassloader(testClass, new String[] { MockClassLoader.MODIFY_ALL_CLASSES },
									getIgnorePackages(testClass));
						} else {
							mockClassloader = createNewClassloader(testClass, arrayMerger.mergeArrays(String.class, prepareForTestExtractor
									.getTestClasses(method), staticSuppressionClasses), getIgnorePackages(testClass));
						}
						// executeClassLoaderDependentMockPolicyMethods(testClass,
						// mockClassloader);
						TestChunk chunk = new TestChunk(mockClassloader, methodsInThisChunk);
						testCaseEntry.getTestChunks().add(chunk);
						updatedIndexes();
					} else {
						testCaseEntry.getTestChunks().get(0).getTestMethodsToBeExecutedByThisClassloader().add(method);
						// currentClassloaderMethods.add(method);
						final int currentDelegateIndex = internalSuites.size() - 1;
						/*
						 * Add this test index to the main junit runner
						 * delegator.
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

	private void updatedIndexes() {
		final List<Integer> testIndexesForThisClassloader = new LinkedList<Integer>();
		testIndexesForThisClassloader.add(currentTestIndex);
		testAtDelegateMapper.put(internalSuites.size(), testIndexesForThisClassloader);
	}

	public int getChunkSize() {
		return getTestChunks().size();
	}

	public List<TestChunk> getTestChunks() {
		List<TestChunk> allChunks = new LinkedList<TestChunk>();
		for (TestCaseEntry entry : internalSuites) {
			for (TestChunk chunk : entry.getTestChunks()) {
				allChunks.add(chunk);
			}
		}
		return allChunks;
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
	 * @return The internal test index as seen by PowerMock or <code>-1</code>
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
	public List<TestChunk> getTestChunksEntries(Class<?> testClass) {
		for (TestCaseEntry entry : internalSuites) {
			if (entry.getTestClass().equals(testClass)) {
				return entry.getTestChunks();
			}
		}
		return null;
	}

	public Class<?>[] getTestClasses() {
		return testClasses;
	}

	/**
	 * Initialize mock policies.
	 */
	protected void initializeMockPolicies(Class<?> testClass, MockClassLoader mockLoader) {
		if (testClass.isAnnotationPresent(MockPolicy.class)) {
			MockPolicy annotation = testClass.getAnnotation(MockPolicy.class);
			final Class<? extends PowerMockPolicy>[] powerMockPolicies = annotation.value();
			if (powerMockPolicies.length > 0) {
				new MockPolicyInitializerImpl(powerMockPolicies).initialize(mockLoader);
			}
		}
	}

	/**
	 * @return <code>true</code> if there are some mock policies that
	 *         contributes with classes that should be loaded by the mock
	 *         classloader, <code>false</code> otherwise.
	 */
	protected boolean hasMockPolicyProvidedClasses(Class<?> testClass) {
		boolean hasMockPolicyProvidedClasses = false;
		if (testClass.isAnnotationPresent(MockPolicy.class)) {
			MockPolicy annotation = testClass.getAnnotation(MockPolicy.class);
			Class<? extends PowerMockPolicy>[] value = annotation.value();
			hasMockPolicyProvidedClasses = new MockPolicyInitializerImpl(value).needsInitialization();
		}
		return hasMockPolicyProvidedClasses;
	}
}
