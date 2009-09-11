package org.powermock.modules.testng;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javassist.util.proxy.ProxyFactory;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.modules.testng.internal.PowerMockTestNGCleanupHandler;
import org.powermock.modules.testng.internal.TestNGMethodFilter;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.utils.IgnorePackagesExtractor;
import org.powermock.tests.utils.TestClassesExtractor;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;
import org.testng.IObjectFactory;

@SuppressWarnings("serial")
public class PowerMockObjectFactory implements IObjectFactory {

    private final MockClassLoader mockLoader;

    private final TestClassesExtractor testClassesExtractor;

    private final IgnorePackagesExtractor ignorePackagesExtractor;

    public PowerMockObjectFactory() {
        List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
        final MainMockTransformer mainMockTransformer = new MainMockTransformer();
        mockTransformerChain.add(mainMockTransformer);

        String[] classesToLoadByMockClassloader = new String[0];
        String[] packagesToIgnore = new String[0];
        mockLoader = new MockClassLoader(classesToLoadByMockClassloader, packagesToIgnore);
        mockLoader.setMockTransformerChain(mockTransformerChain);
        testClassesExtractor = new PrepareForTestExtractorImpl();
        ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();

    }

    @SuppressWarnings("unchecked")
    public Object newInstance(Constructor constructor, Object... params) {
        Class<?> testClass = constructor.getDeclaringClass();
        mockLoader.addIgnorePackage(ignorePackagesExtractor.getPackagesToIgnore(testClass));
        mockLoader.addClassesToModify(testClassesExtractor.getTestClasses(testClass));
        new MockPolicyInitializerImpl(testClass).initialize(mockLoader);
        try {
            final Class<?> testClassLoadedByMockedClassLoader = createTestClassStateCleanupProxy(testClass);
            // TODO listeners of some kind?
            // TODO marshall ctor parameter types?
            Constructor<?> con = testClassLoadedByMockedClassLoader.getConstructor(constructor.getParameterTypes());
            final Object testInstance = con.newInstance(params);
            setInvocationHandler(testInstance);
            return testInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setInvocationHandler(Object testInstance) throws Exception {
        Class<?> powerMockTestNGCleanupHandlerClass = Class.forName(PowerMockTestNGCleanupHandler.class.getName(), false, mockLoader);
        Object powerMockTestNGCleanupHandlerInstance = powerMockTestNGCleanupHandlerClass.newInstance();
        Whitebox.invokeMethod(testInstance, "setHandler", powerMockTestNGCleanupHandlerInstance);
    }

    /**
     * We proxy the test class in order to be able to clear state after each
     * test method invocation. It would be much better to be able to register a
     * testng listener programmtically but I cannot find a way to do so.
     */
    private Class<?> createTestClassStateCleanupProxy(Class<?> actualTestClass) throws Exception {
        Class<?> proxyFactoryClass = Class.forName(ProxyFactory.class.getName(), false, mockLoader);
        final Class<?> testClassLoadedByMockedClassLoader = Class.forName(actualTestClass.getName(), false, mockLoader);
        final Class<?> testNGMethodFilterByMockedClassLoader = Class.forName(TestNGMethodFilter.class.getName(), false, mockLoader);

        Object f = proxyFactoryClass.newInstance();
        Object filter = testNGMethodFilterByMockedClassLoader.newInstance();
        Whitebox.invokeMethod(f, "setFilter", filter);
        Whitebox.invokeMethod(f, "setSuperclass", testClassLoadedByMockedClassLoader);
        Class<?> c = Whitebox.<Class<?>> invokeMethod(f, "createClass");
        return c;
    }
}