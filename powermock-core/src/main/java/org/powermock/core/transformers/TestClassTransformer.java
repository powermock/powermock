/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.core.transformers;

import javassist.CtMethod;
import javassist.NotFoundException;
import org.powermock.core.transformers.javassist.AbstractJavaAssistMockTransformer;
import org.powermock.core.transformers.javassist.ConstructorsMockTransformer;
import org.powermock.core.transformers.javassist.JavaAssistTestClassTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

/**
 * MockTransformer implementation that will make PowerMock test-class
 * enhancements for four purposes...
 * 1) Make test-class static initializer and constructor send crucial details
 * (for PowerMockTestListener events) to GlobalNotificationBuildSupport so that
 * this information can be forwarded to whichever
 * facility is used for composing the PowerMockTestListener events.
 * 2) Removal of test-method annotations as a mean to achieve test-suite
 * chunking!
 * 3) Restore original test-class constructors` accesses
 * (in case they have all been made public by {@link ConstructorsMockTransformer})
 * - to avoid that multiple <i>public</i> test-class constructors cause
 * a delegate runner from JUnit (or 3rd party) to bail out with an
 * error message such as "Test class can only have one constructor".
 * 4) Set test-class defer constructor (if exist) as protected instead of public.
 * Otherwise a delegate runner from JUnit (or 3rd party) might get confused by
 * the presence of more than one test-class constructor and bail out with an
 * error message such as "Test class can only have one constructor".
 * <p>
 * The #3 and #4 enhancements will also be enforced on the constructors
 * of classes that are nested within the test-class.
 */
public abstract class TestClassTransformer<T> implements MockTransformer<T>{
    
    public static ForTestClass forTestClass(final Class<?> testClass) {
        return new ForTestClass() {
            @Override
            public RemovesTestMethodAnnotation removesTestMethodAnnotation(final Class<? extends Annotation> testMethodAnnotation) {
                return new RemovesTestMethodAnnotation() {
                    
                    @Override
                    public TestClassTransformer fromMethods(final Collection<Method> testMethodsThatRunOnOtherClassLoaders) {
                        return new ForMethodsJavaAssistTestClassTransformer(testClass, testMethodAnnotation, testMethodsThatRunOnOtherClassLoaders);
                    }
                    
                    @Override
                    public TestClassTransformer fromAllMethodsExcept(Method singleMethodToRunOnTargetClassLoader) {
                        final String targetMethodSignature = JavaAssistTestClassTransformer.signatureOf(singleMethodToRunOnTargetClassLoader);
                        return new FromAllMethodsExceptJavaAssistTestClassTransformer(testClass, testMethodAnnotation, targetMethodSignature);
                    }
                };
            }
        };
    }
    
    protected final Class<?> testClass;
    protected final Class<? extends Annotation> testMethodAnnotationType;
    
    public TestClassTransformer(Class<?> testClass, Class<? extends Annotation> testMethodAnnotationType) {
        this.testClass = testClass;
        this.testMethodAnnotationType = testMethodAnnotationType;
    }
    
    private static class ForMethodsJavaAssistTestClassTransformer extends JavaAssistTestClassTransformer {
        private final Collection<Method> testMethodsThatRunOnOtherClassLoaders;
        /**
         * Is lazily initilized because of
         * AbstractTestSuiteChunkerImpl#chunkClass(Class)
         */
        Collection<String> methodsThatRunOnOtherClassLoaders;
        
        private ForMethodsJavaAssistTestClassTransformer(final Class<?> testClass, final Class<? extends Annotation> testMethodAnnotation,
                                                         final Collection<Method> testMethodsThatRunOnOtherClassLoaders) {
            super(testClass, testMethodAnnotation);
            this.testMethodsThatRunOnOtherClassLoaders = testMethodsThatRunOnOtherClassLoaders;
        }
        
        @Override
        protected boolean mustHaveTestAnnotationRemoved(CtMethod method) throws NotFoundException {
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
    
    private static class FromAllMethodsExceptJavaAssistTestClassTransformer extends JavaAssistTestClassTransformer {
        private final String targetMethodSignature;
    
        private FromAllMethodsExceptJavaAssistTestClassTransformer(final Class<?> testClass,
                                                           final Class<? extends Annotation> testMethodAnnotation,
                                                           final String targetMethodSignature) {
            super(testClass, testMethodAnnotation);
            this.targetMethodSignature = targetMethodSignature;
        }
        
        @Override
        protected boolean mustHaveTestAnnotationRemoved(CtMethod method) throws Exception {
            return !signatureOf(method).equals(targetMethodSignature);
        }
    }
}
