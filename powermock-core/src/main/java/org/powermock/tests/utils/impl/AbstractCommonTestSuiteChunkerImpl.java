package org.powermock.tests.utils.impl;

import org.powermock.core.classloader.MockClassLoaderFactory;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.TestClassTransformer;
import org.powermock.tests.utils.TestChunk;
import org.powermock.tests.utils.TestSuiteChunker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCommonTestSuiteChunkerImpl implements TestSuiteChunker {
    
    protected static final int NOT_INITIALIZED = -1;
    
    static final int DEFAULT_TEST_LISTENERS_SIZE = 1;
    static final int INTERNAL_INDEX_NOT_FOUND = NOT_INITIALIZED;

    /*
    * Maps between a specific class and a map of test methods loaded by a
    * specific mock class loader.
    */
    private final List<TestCaseEntry> internalSuites = new LinkedList<TestCaseEntry>();
    /*
     * Maps the list of test indexes that is assigned to a specific test suite
     * index.
     */
    final LinkedHashMap<Integer, List<Integer>> testAtDelegateMapper = new LinkedHashMap<Integer, List<Integer>>();
    final Class<?>[] testClasses;
    
    private int currentTestIndex = NOT_INITIALIZED;


    protected AbstractCommonTestSuiteChunkerImpl(Class<?> testClass) throws Exception {
        this(new Class[]{testClass});
    }

    AbstractCommonTestSuiteChunkerImpl(Class<?>... testClasses) throws Exception {
        this.testClasses = testClasses;
        for (Class<?> clazz : testClasses) {
            chunkClass(clazz);
        }
    }

    @Override
    public int getChunkSize() {
        return getTestChunks().size();
    }

    public List<TestChunk> getTestChunks() {
        List<TestChunk> allChunks = new LinkedList<TestChunk>();
        for (TestCaseEntry entry : internalSuites) {
            allChunks.addAll(entry.getTestChunks());
        }
        return allChunks;
    }

    public List<TestChunk> getTestChunksEntries(Class<?> testClass) {
        for (TestCaseEntry entry : internalSuites) {
            if (entry.getTestClass().equals(testClass)) {
                return entry.getTestChunks();
            }
        }
        return null;
    }

    public TestChunk getTestChunk(Method method) {
        for (TestChunk testChunk : getTestChunks()) {
            if (testChunk.isMethodToBeExecutedByThisClassloader(method)) {
                return testChunk;
            }
        }
        return null;
    }
    
    private void chunkClass(final Class<?> testClass) throws Exception {

        List<Method> testMethodsForOtherClassLoaders = new ArrayList<Method>();
        
        final ClassLoader defaultMockLoader = createDefaultMockLoader(testClass, testMethodsForOtherClassLoaders);
    
        List<Method> currentClassloaderMethods = new LinkedList<Method>();
        TestChunk defaultTestChunk = new TestChunkImpl(defaultMockLoader, currentClassloaderMethods);
        
        // Put the first suite in the map of internal suites.
        List<TestChunk> testChunks = new LinkedList<TestChunk>();
        testChunks.add(defaultTestChunk);
        
        internalSuites.add(new TestCaseEntry(testClass, testChunks));
        initEntries(internalSuites);
        
        if (!currentClassloaderMethods.isEmpty()) {
            List<TestChunk> allTestChunks = internalSuites.get(0).getTestChunks();
            for (TestChunk chunk : allTestChunks.subList(1, allTestChunks.size())) {
                testMethodsForOtherClassLoaders.addAll(chunk.getTestMethodsToBeExecutedByThisClassloader());
            }
        } else if (2 <= internalSuites.size()
                           || 1 == internalSuites.size()
                                      && 2 <= internalSuites.get(0).getTestChunks().size()) {
            /*
             * If we don't have any test that should be executed by the default
             * class loader remove it to avoid duplicate test print outs.
             */
            internalSuites.get(0).getTestChunks().remove(0);
        }
        //else{ /*Delegation-runner maybe doesn't use test-method annotations!*/ }
    }
    
    private ClassLoader createDefaultMockLoader(final Class<?> testClass, final Collection<Method> testMethodsForOtherClassLoaders) {
        final MockTransformer extraMockTransformer;
        if (null == testMethodAnnotation()) {
            extraMockTransformer = null;
        } else {
            extraMockTransformer = TestClassTransformer
                                       .forTestClass(testClass)
                                       .removesTestMethodAnnotation(testMethodAnnotation())
                                       .fromMethods(testMethodsForOtherClassLoaders);
        }
        return new MockClassLoaderFactory(testClass).createForClass(extraMockTransformer);
    }
    
    private void putMethodToChunk(TestCaseEntry testCaseEntry, Class<?> testClass, Method method) {
        if (shouldExecuteTestForMethod(testClass, method)) {
            currentTestIndex++;
            if (hasChunkAnnotation(method)) {
                LinkedList<Method> methodsInThisChunk = new LinkedList<Method>();
                methodsInThisChunk.add(method);
                
                final ClassLoader mockClassloader = createClassLoaderForMethod(testClass, method);
                
                final TestChunkImpl chunk = new TestChunkImpl(mockClassloader, methodsInThisChunk);
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
    
    private ClassLoader createClassLoaderForMethod(final Class<?> testClass, final Method method) {
        
        final MockTransformer extraMockTransformer;
        if (null == testMethodAnnotation()) {
            extraMockTransformer = null;
        } else {
            extraMockTransformer = TestClassTransformer.forTestClass(testClass)
                                                       .removesTestMethodAnnotation(testMethodAnnotation())
                                                       .fromAllMethodsExcept(method);
        }
        
        final MockClassLoaderFactory classLoaderFactory = new MockClassLoaderFactory(testClass);
        return classLoaderFactory.createForMethod(method, extraMockTransformer);
    }
    
    protected Class<? extends Annotation> testMethodAnnotation() {
        return null;
    }
    
    private void initEntries(List<TestCaseEntry> entries) {
        for (TestCaseEntry testCaseEntry : entries) {
            final Class<?> testClass = testCaseEntry.getTestClass();
            findMethods(testCaseEntry, testClass);
        }
    }
    
    private void findMethods(TestCaseEntry testCaseEntry, Class<?> testClass) {
        Method[] allMethods = testClass.getMethods();
        for (Method method : allMethods) {
            putMethodToChunk(testCaseEntry, testClass, method);
        }
        testClass = testClass.getSuperclass();
        if (!Object.class.equals(testClass)) {
            findMethods(testCaseEntry, testClass);
        }
    }

    private boolean hasChunkAnnotation(Method method) {
        return method.isAnnotationPresent(PrepareForTest.class) || method.isAnnotationPresent(SuppressStaticInitializationFor.class)
                       || method.isAnnotationPresent(PrepareOnlyThisForTest.class) || method.isAnnotationPresent(PrepareEverythingForTest.class);
    }

    private void updatedIndexes() {
        final List<Integer> testIndexesForThisClassloader = new LinkedList<Integer>();
        testIndexesForThisClassloader.add(currentTestIndex);
        testAtDelegateMapper.put(internalSuites.size(), testIndexesForThisClassloader);
    }

}
