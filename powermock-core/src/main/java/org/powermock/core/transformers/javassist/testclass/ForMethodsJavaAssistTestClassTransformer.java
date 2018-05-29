package org.powermock.core.transformers.javassist.testclass;

import javassist.CtMethod;
import org.powermock.core.transformers.MethodSignatureWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ForMethodsJavaAssistTestClassTransformer extends JavaAssistTestClassTransformer {
    
    private final Collection<Method> testMethodsThatRunOnOtherClassLoaders;
    /**
     * Is lazily initilized because of
     * AbstractTestSuiteChunkerImpl#chunkClass(Class)
     */
    private Collection<String> methodsThatRunOnOtherClassLoaders;
    
    public ForMethodsJavaAssistTestClassTransformer(final Class<?> testClass,
                                                    final Class<? extends Annotation> testMethodAnnotation,
                                                    final MethodSignatureWriter<CtMethod> methodSignatureWriter,
                                                    final Collection<Method> testMethodsThatRunOnOtherClassLoaders) {
        super(testClass, testMethodAnnotation, methodSignatureWriter);
        this.testMethodsThatRunOnOtherClassLoaders = testMethodsThatRunOnOtherClassLoaders;
    }
    
    @Override
    protected boolean mustHaveTestAnnotationRemoved(CtMethod method) throws Exception {
        if (null == methodsThatRunOnOtherClassLoaders) {
            /* This lazy initialization is necessary - see above */
            methodsThatRunOnOtherClassLoaders = new HashSet<String>();
            for (Method m : testMethodsThatRunOnOtherClassLoaders) {
                methodsThatRunOnOtherClassLoaders.add(signatureOf(m));
            }
            testMethodsThatRunOnOtherClassLoaders.clear();
        }
        return methodsThatRunOnOtherClassLoaders.contains(signatureOf(method));
    }
    
}
