/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.tests.utils.impl;

import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.reporter.MockingFrameworkReporterFactory;
import org.powermock.core.spi.PowerMockTestListener;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.RunnerTestSuiteChunker;
import org.powermock.tests.utils.TestChunk;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract base class for test suite chunking, i.e. a suite is chunked into
 * several smaller pieces which are ran with different classloaders. A chunk is
 * defined by the {@link PrepareForTest} annotation and whichever test-method
 * annotation the actual implementation-class specifies by overriding the
 * method {@link #testMethodAnnotation()}. This to make sure that you
 * can byte-code manipulate classes in tests without impacting on other tests.
 * 
 */
public abstract class AbstractTestSuiteChunkerImpl<T> extends AbstractCommonTestSuiteChunkerImpl implements RunnerTestSuiteChunker {

    /*
     * The classes listed in this set has been chunked and its delegates has
     * been created.
     */
    protected final Set<Class<?>> delegatesCreatedForTheseClasses = new LinkedHashSet<Class<?>>();

    // A list of junit delegates.
    protected final List<T> delegates = new ArrayList<T>();

    protected volatile int testCount = NOT_INITIALIZED;

    protected AbstractTestSuiteChunkerImpl(Class<?> testClass) throws Exception {
        super(testClass);
    }

    protected AbstractTestSuiteChunkerImpl(Class<?>... testClasses) throws Exception {
        super(testClasses);
    }

    protected Object getPowerMockTestListenersLoadedByASpecificClassLoader(Class<?> clazz, ClassLoader classLoader) {
        try {
            int defaultListenerSize = DEFAULT_TEST_LISTENERS_SIZE;
            Class<?> annotationEnablerClass = null;
            try {
                annotationEnablerClass = Class.forName("org.powermock.api.extension.listener.AnnotationEnabler", false, classLoader);
            } catch (ClassNotFoundException e) {
                // Annotation enabler wasn't found in class path
                defaultListenerSize = 0;
            }

            final Class<?> powerMockTestListenerType = Class.forName(PowerMockTestListener.class.getName(), false, classLoader);
            Object testListeners = null;
            if (clazz.isAnnotationPresent(PowerMockListener.class)) {
                PowerMockListener annotation = clazz.getAnnotation(PowerMockListener.class);
                final Class<? extends PowerMockTestListener>[] powerMockTestListeners = annotation.value();
                if (powerMockTestListeners.length > 0) {
                    testListeners = Array.newInstance(powerMockTestListenerType, powerMockTestListeners.length + defaultListenerSize);
                    for (int i = 0; i < powerMockTestListeners.length; i++) {
                        String testListenerClassName = powerMockTestListeners[i].getName();
                        final Class<?> listenerTypeLoadedByClassLoader = Class.forName(testListenerClassName, false, classLoader);
                        Array.set(testListeners, i, Whitebox.newInstance(listenerTypeLoadedByClassLoader));
                    }
                }
            } else {
                testListeners = Array.newInstance(powerMockTestListenerType, defaultListenerSize);
            }

            // Add default annotation enabler listener
            if (annotationEnablerClass != null) {
                Array.set(testListeners, Array.getLength(testListeners) - 1, Whitebox.newInstance(annotationEnablerClass));
            }

            return testListeners;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PowerMock internal error: Failed to load class.", e);
        }
    }

    public final void createTestDelegators(Class<?> testClass, List<TestChunk> chunks) throws Exception {
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
     * @return The internal test index as seen by PowerMock or {@code -1}
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
     * {@code testIndex}. Throws a {@link RuntimeException} if a delegator
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

    public Class<?>[] getTestClasses() {
        return testClasses;
    }


    @SuppressWarnings("unchecked")
    protected MockingFrameworkReporterFactory getFrameworkReporterFactory() {
        Class<MockingFrameworkReporterFactory> mockingFrameworkReporterFactoryClass;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            mockingFrameworkReporterFactoryClass = (Class<MockingFrameworkReporterFactory>) classLoader.loadClass("org.powermock.api.extension.reporter.MockingFrameworkReporterFactoryImpl");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                                                   "Extension API internal error: org.powermock.api.extension.reporter.MockingFrameworkReporterFactoryImpl could not be located in classpath.");
        }

        return Whitebox.newInstance(mockingFrameworkReporterFactoryClass);
    }

}
