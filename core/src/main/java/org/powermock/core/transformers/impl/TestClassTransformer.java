/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.core.transformers.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.testlisteners.GlobalNotificationBuildSupport;
import org.powermock.core.transformers.MockTransformer;

/**
 * MockTransformer implementation that will make PowerMock test-class
 * enhancements for three purposes...
 * 1) Make test-class static initializer and constructor send crucial details
 * (for PowerMockTestListener events) to GlobalNotificationBuildSupport so that
 * this information can be forwarded to whichever
 * facility is used for composing the PowerMockTestListener events.
 * 2) Removal of test-method annotations as a mean to achieve test-suite
 * chunking!
 * 3) Set test-class defer constructor (if exist) as protected instead of public.
 * Otherwise a delegate runner from JUnit (or 3rd party) might get confused by
 * the presence of more than one test-class constructor and bail out with an
 * error message such as "Test class can only have one constructor".
 */
public abstract class TestClassTransformer implements MockTransformer {

    private final Class<?> testClass;
    private final Class<? extends Annotation> testMethodAnnotationType;

    public interface ForTestClass {
        RemovesTestMethodAnnotation removesTestMethodAnnotation(Class<? extends Annotation> testMethodAnnotation);

        public interface RemovesTestMethodAnnotation {
            TestClassTransformer fromMethods(Collection<Method> testMethodsThatRunOnOtherClassLoaders);
            TestClassTransformer fromAllMethodsExcept(Method singleMethodToRunOnThisClassLoader);
        }
    }

    public static ForTestClass forTestClass(final Class<?> testClass) {
        return new ForTestClass() {
            public RemovesTestMethodAnnotation removesTestMethodAnnotation(
                    final Class<? extends Annotation> testMethodAnnotation) {
                return new RemovesTestMethodAnnotation() {

                    public TestClassTransformer fromMethods(
                            final Collection<Method> testMethodsThatRunOnOtherClassLoaders) {
                        return new TestClassTransformer(testClass, testMethodAnnotation) {
                            /**
                             * Is lazily initilized because of
                             * AbstractTestSuiteChunkerImpl#chunkClass(Class)
                             */
                            Collection<String> methodsThatRunOnOtherClassLoaders;
                            @Override
                            boolean mustHaveTestAnnotationRemoved(CtMethod method)
                            throws NotFoundException {
                                if (null == methodsThatRunOnOtherClassLoaders) {
                                    /* This lazy initialization is necessary - see above */
                                    methodsThatRunOnOtherClassLoaders = new HashSet<String>();
                                    for (Method m : testMethodsThatRunOnOtherClassLoaders) {
                                        methodsThatRunOnOtherClassLoaders.add(
                                                signatureOf(m));
                                    }
                                    testMethodsThatRunOnOtherClassLoaders.clear();
                                }
                                return methodsThatRunOnOtherClassLoaders
                                        .contains(signatureOf(method));
                            }                            
                        };
                    }

                    public TestClassTransformer fromAllMethodsExcept(
                            Method singleMethodToRunOnTargetClassLoader) {
                        final String targetMethodSignature =
                                signatureOf(singleMethodToRunOnTargetClassLoader);
                        return new TestClassTransformer(testClass, testMethodAnnotation) {
                            @Override
                            boolean mustHaveTestAnnotationRemoved(CtMethod method)
                            throws Exception {
                                return false == signatureOf(method)
                                        .equals(targetMethodSignature);
                            }
                        };
                    }
                };
            }
        };
    }

    private TestClassTransformer(
            Class<?> testClass, Class<? extends Annotation> testMethodAnnotationType) {
        this.testClass = testClass;
        this.testMethodAnnotationType = testMethodAnnotationType;
    }

    private boolean isTestClass(CtClass clazz) {
        try {
            return Class.forName(clazz.getName(), false, testClass.getClassLoader())
                    .isAssignableFrom(testClass);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    abstract boolean mustHaveTestAnnotationRemoved(CtMethod method) throws Exception;

    private void removeTestMethodAnnotationFrom(CtMethod m)
    throws ClassNotFoundException {
        final AnnotationsAttribute attr = (AnnotationsAttribute)
                m.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation[] newAnnotations =
                new javassist.bytecode.annotation.Annotation[attr.numAnnotations() - 1];
        int i = -1;
        for (javassist.bytecode.annotation.Annotation a : attr.getAnnotations()) {
            if (a.getTypeName().equals(testMethodAnnotationType.getName())) {
                continue;
            }
            newAnnotations[++i] = a;
        }
        attr.setAnnotations(newAnnotations);
    }

    private void removeTestAnnotationsForTestMethodsThatRunOnOtherClassLoader(CtClass clazz)
    throws Exception {
        for (CtMethod m : clazz.getDeclaredMethods()) {
            if (m.hasAnnotation(testMethodAnnotationType)
                    && mustHaveTestAnnotationRemoved(m)) {
                removeTestMethodAnnotationFrom(m);
            }
        }
    }

    public CtClass transform(final CtClass clazz) throws Exception {
        if (clazz.isFrozen()) {
            clazz.defrost();
        }

        if (isTestClass(clazz)) {
            removeTestAnnotationsForTestMethodsThatRunOnOtherClassLoader(clazz);
            addLifeCycleNotifications(clazz);
            makeDeferConstructorNonPublic(clazz);
        }

        return clazz;
    }

    private void addLifeCycleNotifications(CtClass clazz) {
        try {
            addClassInitializerNotification(clazz);
            addConstructorNotification(clazz);
        } catch (CannotCompileException ex) {
            throw new Error("Powermock error: " + ex.getMessage(), ex);
        }
    }

    private void addClassInitializerNotification(CtClass clazz)
    throws CannotCompileException {
        if (null == clazz.getClassInitializer()) {
            clazz.makeClassInitializer();
        }
        clazz.getClassInitializer().insertBefore(
                    GlobalNotificationBuildSupport.class.getName()
                    + ".testClassInitiated(" + clazz.getName() + ".class);");
    }

    private static boolean hasSuperClass(CtClass clazz) {
        try {
            CtClass superClazz = clazz.getSuperclass();
            /*
             * Being extra careful here - and backup in case the
             * work-in-progress clazz doesn't cause NotFoundException ...
             */
            return null != superClazz
                    && false == "java.lang.Object".equals(superClazz.getName());
        } catch (NotFoundException noWasSuperClassFound) {
            return false;
        }
    }

    private void addConstructorNotification(final CtClass clazz)
    throws CannotCompileException {
        final String notificationCode =
                GlobalNotificationBuildSupport.class.getName()
                + ".testInstanceCreated(this);";
        final boolean asFinally = false == hasSuperClass(clazz);
        for (final CtConstructor constr : clazz.getDeclaredConstructors()) {
            constr.insertAfter(
                    notificationCode,
                    asFinally/* unless there is a super-class, because of this
                              * problem: https://community.jboss.org/thread/94194*/);
        }
    }

    private void makeDeferConstructorNonPublic(final CtClass clazz) {
        for (final CtConstructor constr : clazz.getConstructors()) {
            try {
                for (CtClass paramType : constr.getParameterTypes()) {
                    if (IndicateReloadClass.class.getName()
                            .equals(paramType.getName())) {
                        /* Found defer constructor ... */
                        final int modifiers = constr.getModifiers();
                        if (Modifier.isPublic(modifiers)) {
                            constr.setModifiers(Modifier.setProtected(modifiers));
                        }
                        break;
                    }
                }
            } catch (NotFoundException thereAreNoParameters) {
                /* ... but to get an exception here seems odd. */
            }
        }
    }

    private static String signatureOf(Method m) {
        Class<?>[] paramTypes = m.getParameterTypes();
        String[] paramTypeNames = new String[paramTypes.length];
        for (int i = 0; i < paramTypeNames.length; ++i) {
            paramTypeNames[i] = paramTypes[i].getSimpleName();
        }
        return createSignature(
                m.getDeclaringClass().getSimpleName(),
                m.getReturnType().getSimpleName(),
                m.getName(), paramTypeNames);
    }

    private static String signatureOf(CtMethod m) throws NotFoundException {
        CtClass[] paramTypes = m.getParameterTypes();
        String[] paramTypeNames = new String[paramTypes.length];
        for (int i = 0; i < paramTypeNames.length; ++i) {
            paramTypeNames[i] = paramTypes[i].getSimpleName();
        }
        return createSignature(
                m.getDeclaringClass().getSimpleName(),
                m.getReturnType().getSimpleName(),
                m.getName(), paramTypeNames);
    }

    private static String createSignature(
            String testClass, String returnType, String methodName, String[] paramTypes) {
        StringBuilder builder = new StringBuilder(testClass)
                .append('\n').append(returnType)
                .append('\n').append(methodName);
        for (String param : paramTypes) {
            builder.append('\n').append(param);
        }
        return builder.toString();
    }
}
