package org.powermock.core.transformers;

/**
 * If a transformer implements this interface then {@link org.powermock.core.classloader.MockClassLoaderBuilder} sets current test class during building a instance of {@link org.powermock.core.classloader.MockClassLoader}.
 * <b>IMPORTANT</b>
 * This may take affect only with running PowerMock with class loader mode, a <code>testClass</code> will not be set in case if PowerMock used as JavaAgent.
 */
public interface TestClassAwareTransformer {
    void setTestClass(Class<?> testClass);
}
