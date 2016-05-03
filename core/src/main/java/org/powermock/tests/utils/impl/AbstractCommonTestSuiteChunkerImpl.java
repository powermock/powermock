package org.powermock.tests.utils.impl;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.TestClassTransformer;
import org.powermock.tests.utils.ArrayMerger;
import org.powermock.tests.utils.IgnorePackagesExtractor;
import org.powermock.tests.utils.TestChunk;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.TestSuiteChunker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public abstract class AbstractCommonTestSuiteChunkerImpl implements TestSuiteChunker {

    protected static final int DEFAULT_TEST_LISTENERS_SIZE = 1;

    protected static final int NOT_INITIALIZED = -1;

    protected static final int INTERNAL_INDEX_NOT_FOUND = NOT_INITIALIZED;

    /*
    * Maps between a specific class and a map of test methods loaded by a
    * specific mock class loader.
    */
    private final List<TestCaseEntry> internalSuites = new LinkedList<TestCaseEntry>();
    private final TestClassesExtractor prepareForTestExtractor = new PrepareForTestExtractorImpl();
    private final TestClassesExtractor suppressionExtractor = new StaticConstructorSuppressExtractorImpl();
    /*
     * Maps the list of test indexes that is assigned to a specific test suite
     * index.
     */
    protected final LinkedHashMap<Integer, List<Integer>> testAtDelegateMapper = new LinkedHashMap<Integer, List<Integer>>();
    protected final Class<?>[] testClasses;
    private final IgnorePackagesExtractor ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();
    private final ArrayMerger arrayMerger = new ArrayMergerImpl();
    private int currentTestIndex = NOT_INITIALIZED;


    protected AbstractCommonTestSuiteChunkerImpl(Class<?> testClass) throws Exception {
        this(new Class[]{testClass});
    }

    protected AbstractCommonTestSuiteChunkerImpl(Class<?>... testClasses) throws Exception {
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
            for (TestChunk chunk : entry.getTestChunks()) {
                allChunks.add(chunk);
            }
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

    protected void chunkClass(final Class<?> testClass) throws Exception {

        List<Method> testMethodsForOtherClassLoaders = new ArrayList<Method>();

        MockTransformer[] extraMockTransformers = createDefaultExtraMockTransformers(testClass, testMethodsForOtherClassLoaders);

        final String[] ignorePackages = ignorePackagesExtractor.getPackagesToIgnore(testClass);

        final ClassLoader defaultMockLoader = createDefaultMockLoader(testClass, extraMockTransformers, ignorePackages);

        List<Method> currentClassloaderMethods = new LinkedList<Method>();
        // Put the first suite in the map of internal suites.
        TestChunk defaultTestChunk = new TestChunkImpl(defaultMockLoader, currentClassloaderMethods);
        List<TestChunk> testChunks = new LinkedList<TestChunk>();
        testChunks.add(defaultTestChunk);
        internalSuites.add(new TestCaseEntry(testClass, testChunks));
        initEntries(internalSuites);

        if (!currentClassloaderMethods.isEmpty()) {
            List<TestChunk> allTestChunks = internalSuites.get(0).getTestChunks();
            for (TestChunk chunk : allTestChunks.subList(1, allTestChunks.size())) {
                for (Method m : chunk.getTestMethodsToBeExecutedByThisClassloader()) {
                    testMethodsForOtherClassLoaders.add(m);
                }
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

    private ClassLoader createDefaultMockLoader(Class<?> testClass, MockTransformer[] extraMockTransformers, String[] ignorePackages) {
        final ClassLoader defaultMockLoader;
        if (testClass.isAnnotationPresent(PrepareEverythingForTest.class)) {
            defaultMockLoader = createNewClassloader(testClass, new String[]{MockClassLoader.MODIFY_ALL_CLASSES},
                    ignorePackages, extraMockTransformers);
        } else {
            final String[] prepareForTestClasses = prepareForTestExtractor.getTestClasses(testClass);
            final String[] suppressStaticClasses = suppressionExtractor.getTestClasses(testClass);
            defaultMockLoader = createNewClassloader(testClass, arrayMerger.mergeArrays(String.class, prepareForTestClasses, suppressStaticClasses),
                    ignorePackages, extraMockTransformers);
        }
        return defaultMockLoader;
    }

    private ClassLoader createNewClassloader(Class<?> testClass, String[] classesToLoadByMockClassloader,
                                             final String[] packagesToIgnore, MockTransformer... extraMockTransformers) {
        final MockClassLoaderFactory classLoaderFactory = getMockClassLoaderFactory(testClass, classesToLoadByMockClassloader, packagesToIgnore, extraMockTransformers);
        return classLoaderFactory.create();
    }

    protected MockClassLoaderFactory getMockClassLoaderFactory(Class<?> testClass, String[] preliminaryClassesToLoadByMockClassloader, String[] packagesToIgnore, MockTransformer[] extraMockTransformers) {
        return new MockClassLoaderFactory(testClass, preliminaryClassesToLoadByMockClassloader, packagesToIgnore, extraMockTransformers);
    }

    private MockTransformer[] createDefaultExtraMockTransformers(Class<?> testClass, List<Method> testMethodsThatRunOnOtherClassLoaders) {
        if (null == testMethodAnnotation()) {
            return new MockTransformer[0];
        } else {
            return new MockTransformer[]{
                    TestClassTransformer
                            .forTestClass(testClass)
                            .removesTestMethodAnnotation(testMethodAnnotation())
                            .fromMethods(testMethodsThatRunOnOtherClassLoaders)
            };
        }
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

    private void putMethodToChunk(TestCaseEntry testCaseEntry, Class<?> testClass, Method method) {
        if (shouldExecuteTestForMethod(testClass, method)) {
            currentTestIndex++;
            if (hasChunkAnnotation(method)) {
                LinkedList<Method> methodsInThisChunk = new LinkedList<Method>();
                methodsInThisChunk.add(method);
                final String[] staticSuppressionClasses = getStaticSuppressionClasses(testClass, method);
                TestClassTransformer[] extraTransformers = null == testMethodAnnotation()
                                                                   ? new TestClassTransformer[0]
                                                                   : new TestClassTransformer[]{
                        TestClassTransformer.forTestClass(testClass)
                                            .removesTestMethodAnnotation(testMethodAnnotation())
                                .fromAllMethodsExcept(method)
                };

                final ClassLoader mockClassloader;
                if (method.isAnnotationPresent(PrepareEverythingForTest.class)) {
                    mockClassloader = createNewClassloader(testClass, new String[]{MockClassLoader.MODIFY_ALL_CLASSES},
                            ignorePackagesExtractor.getPackagesToIgnore(testClass), extraTransformers);
                } else {
                    mockClassloader = createNewClassloader(testClass, arrayMerger.mergeArrays(String.class, prepareForTestExtractor.getTestClasses(method),
                            staticSuppressionClasses), ignorePackagesExtractor.getPackagesToIgnore(testClass), extraTransformers);
                }
                TestChunkImpl chunk = new TestChunkImpl(mockClassloader, methodsInThisChunk);
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

    private boolean hasChunkAnnotation(Method method) {
        return method.isAnnotationPresent(PrepareForTest.class) || method.isAnnotationPresent(SuppressStaticInitializationFor.class)
                       || method.isAnnotationPresent(PrepareOnlyThisForTest.class) || method.isAnnotationPresent(PrepareEverythingForTest.class);
    }

    private String[] getStaticSuppressionClasses(Class<?> testClass, Method method) {
        final String[] testClasses;
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

}
