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

import org.powermock.core.transformers.javassist.ConstructorsMockTransformer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
public abstract class TestClassTransformer<T, M> implements MockTransformer<T> {
    
    private final Class<?> testClass;
    private final Class<? extends Annotation> testMethodAnnotationType;
    private final MethodSignatureWriter<M> methodSignatureWriter;
    
    public TestClassTransformer(Class<?> testClass, Class<? extends Annotation> testMethodAnnotationType,
                                MethodSignatureWriter<M> methodSignatureWriter) {
        this.testClass = testClass;
        this.testMethodAnnotationType = testMethodAnnotationType;
        this.methodSignatureWriter = methodSignatureWriter;
    }
    
    protected String signatureOf(final M method) {
        return methodSignatureWriter.signatureFor(method);
    }
    
    protected String signatureOf(final Method m) {
        return methodSignatureWriter.signatureForReflection(m);
    }
    
    protected Class<? extends Annotation> getTestMethodAnnotationType() {
        return testMethodAnnotationType;
    }
    
    protected Class<?> getTestClass() {
        return testClass;
    }
}
