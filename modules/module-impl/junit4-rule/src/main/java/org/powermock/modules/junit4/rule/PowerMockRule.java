package org.powermock.modules.junit4.rule;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.powermock.classloading.ClassloaderExecutor;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.proxyframework.RegisterProxyFramework;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;
import org.powermock.tests.utils.impl.PowerMockIgnorePackagesExtractorImpl;
import org.powermock.tests.utils.impl.PrepareForTestExtractorImpl;

public class PowerMockRule implements MethodRule {
    private static ClassloaderExecutor classloaderExecutor;
    private static Class<?> previousTargetClass;
    
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		if (classloaderExecutor == null || previousTargetClass != target.getClass()) {
			classloaderExecutor = makeClassloaderExecutor(target);
			previousTargetClass = target.getClass();
		}
		return new PowerMockStatement(base, classloaderExecutor);
	}

	private ClassloaderExecutor makeClassloaderExecutor(Object target) {
		List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
        final MainMockTransformer mainMockTransformer = new MainMockTransformer();
        mockTransformerChain.add(mainMockTransformer);

        String[] classesToLoadByMockClassloader = new String[0];
        String[] packagesToIgnore = new String[0];
        MockClassLoader mockLoader = new MockClassLoader(classesToLoadByMockClassloader, packagesToIgnore);
        mockLoader.setMockTransformerChain(mockTransformerChain);
        PrepareForTestExtractorImpl testClassesExtractor = new PrepareForTestExtractorImpl();
        PowerMockIgnorePackagesExtractorImpl ignorePackagesExtractor = new PowerMockIgnorePackagesExtractorImpl();

        final Class<? extends Object> testClass = target.getClass();
        mockLoader.addIgnorePackage(ignorePackagesExtractor.getPackagesToIgnore(testClass));
        mockLoader.addClassesToModify(testClassesExtractor.getTestClasses(target.getClass()));
        registerProxyframework(mockLoader);
        new MockPolicyInitializerImpl(testClass).initialize(mockLoader);
        ClassloaderExecutor classloaderExecutor = new ClassloaderExecutor(mockLoader);
		return classloaderExecutor;
	}

    private void registerProxyframework(ClassLoader classLoader) {
        Class<?> proxyFrameworkClass = null;
        try {
            proxyFrameworkClass = Class.forName("org.powermock.api.extension.proxyframework.ProxyFrameworkImpl", false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Extension API internal error: org.powermock.api.extension.proxyframework.ProxyFrameworkImpl could not be located in classpath.");
        }

        Class<?> proxyFrameworkRegistrar = null;
        try {
            proxyFrameworkRegistrar = Class.forName(RegisterProxyFramework.class.getName(), false, classLoader);
        } catch (ClassNotFoundException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        try {
            Whitebox.invokeMethod(proxyFrameworkRegistrar, "registerProxyFramework", Whitebox.newInstance(proxyFrameworkClass));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

class PowerMockStatement extends Statement {
    private final Statement fNext;
	private final ClassloaderExecutor classloaderExecutor;

    public PowerMockStatement(Statement base, ClassloaderExecutor classloaderExecutor) {
        fNext = base;
		this.classloaderExecutor = classloaderExecutor;
    }

    @Override
    public void evaluate() throws Throwable {
        classloaderExecutor.execute(new Runnable() {
            public void run() {
                try {
                		fNext.evaluate();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
