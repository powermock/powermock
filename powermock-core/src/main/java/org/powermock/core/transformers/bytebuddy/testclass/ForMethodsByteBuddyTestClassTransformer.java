package org.powermock.core.transformers.bytebuddy.testclass;

import net.bytebuddy.description.method.MethodDescription;
import org.powermock.core.transformers.MethodSignatureWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

public class ForMethodsByteBuddyTestClassTransformer extends ByteBuddyTestClassTransformer {
    
    private final Collection<Method> testMethodsThatRunOnOtherClassLoaders;
    /**
     * Is lazily initilized because of
     * AbstractTestSuiteChunkerImpl#chunkClass(Class)
     */
    private Collection<String> methodsThatRunOnOtherClassLoaders;
    
    public ForMethodsByteBuddyTestClassTransformer(final Class<?> testClass,
                                                   final Class<? extends Annotation> testMethodAnnotation,
                                                   final MethodSignatureWriter<MethodDescription> methodSignatureWriter,
                                                   final Collection<Method> testMethodsThatRunOnOtherClassLoaders) {
        super(testClass, testMethodAnnotation, methodSignatureWriter);
        this.testMethodsThatRunOnOtherClassLoaders = testMethodsThatRunOnOtherClassLoaders;
    }
    
    @Override
    public boolean mustHaveTestAnnotationRemoved(MethodDescription method) {
        if (null == methodsThatRunOnOtherClassLoaders) {
            methodsThatRunOnOtherClassLoaders = new HashSet<String>();
            for (Method m : testMethodsThatRunOnOtherClassLoaders) {
                methodsThatRunOnOtherClassLoaders.add(signatureOf(m));
            }
            testMethodsThatRunOnOtherClassLoaders.clear();
        }
        return methodsThatRunOnOtherClassLoaders.contains(signatureOf(method));
    }
}
