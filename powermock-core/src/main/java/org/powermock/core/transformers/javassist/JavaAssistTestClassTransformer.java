/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.powermock.core.transformers.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.testlisteners.GlobalNotificationBuildSupport;
import org.powermock.core.transformers.ClassWrapper;
import org.powermock.core.transformers.TestClassTransformer;
import org.powermock.core.transformers.javassist.support.Primitives;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public abstract class JavaAssistTestClassTransformer extends TestClassTransformer<CtClass> {
    
    protected JavaAssistTestClassTransformer(Class<?> testClass, Class<? extends Annotation> testMethodAnnotationType) {
        super(testClass, testMethodAnnotationType);
    }
    
    protected abstract boolean mustHaveTestAnnotationRemoved(CtMethod method) throws Exception;
    
    @Override
    public ClassWrapper<CtClass> transform(final ClassWrapper<CtClass> clazz) throws Exception {
            transform(clazz.unwrap());
            return clazz;
    }
    
    private void transform(final CtClass clazz) throws Exception {
        if (clazz.isFrozen()) {
            clazz.defrost();
        }
        
        if (isTestClass(clazz)) {
            removeTestAnnotationsForTestMethodsThatRunOnOtherClassLoader(clazz);
            addLifeCycleNotifications(clazz);
            makeDeferConstructorNonPublic(clazz);
            restoreOriginalConstructorsAccesses(clazz);
            
        } else if (isNestedWithinTestClass(clazz)) {
            makeDeferConstructorNonPublic(clazz);
            restoreOriginalConstructorsAccesses(clazz);
        }
    
    }
    
    private boolean isTestClass(CtClass clazz) {
        try {
            return Class.forName(clazz.getName(), false, testClass.getClassLoader())
                        .isAssignableFrom(testClass);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private boolean isNestedWithinTestClass(CtClass clazz) {
        String clazzName = clazz.getName();
        return clazzName.startsWith(testClass.getName())
                   && '$' == clazzName.charAt(testClass.getName().length());
    }
    
    private Class<?> asOriginalClass(CtClass type) throws Exception {
        try {
            return type.isArray()
                       ? Array.newInstance(asOriginalClass(type.getComponentType()), 0).getClass()
                       : type.isPrimitive()
                             ? Primitives.getClassFor((CtPrimitiveType) type)
                             : Class.forName(type.getName(), true, testClass.getClassLoader());
        } catch (Exception ex) {
            throw new RuntimeException("Cannot resolve type: " + type, ex);
        }
    }
    
    private Class<?>[] asOriginalClassParams(CtClass[] parameterTypes)
        throws Exception {
        final Class<?>[] classParams = new Class[parameterTypes.length];
        for (int i = 0; i < classParams.length; ++i) {
            classParams[i] = asOriginalClass(parameterTypes[i]);
        }
        return classParams;
    }
    
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
            if (m.hasAnnotation(testMethodAnnotationType) && mustHaveTestAnnotationRemoved(m)) {
                removeTestMethodAnnotationFrom(m);
            }
        }
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
                       && !"java.lang.Object".equals(superClazz.getName());
        } catch (NotFoundException noWasSuperClassFound) {
            return false;
        }
    }
    
    private void addConstructorNotification(final CtClass clazz)
        throws CannotCompileException {
        final String notificationCode =
            GlobalNotificationBuildSupport.class.getName()
                + ".testInstanceCreated(this);";
        final boolean asFinally = !hasSuperClass(clazz);
        for (final CtConstructor constr : clazz.getDeclaredConstructors()) {
            constr.insertAfter(
                notificationCode,
                asFinally/* unless there is a super-class, because of this
                              * problem: https://community.jboss.org/thread/94194*/);
        }
    }
    
    private void restoreOriginalConstructorsAccesses(CtClass clazz) throws Exception {
        Class<?> originalClass = testClass.getName().equals(clazz.getName())
                                     ? testClass
                                     : Class.forName(clazz.getName(), true, testClass.getClassLoader());
        for (final CtConstructor ctConstr : clazz.getConstructors()) {
            int ctModifiers = ctConstr.getModifiers();
            if (!Modifier.isPublic(ctModifiers)) {
                /* Probably a defer-constructor */
                continue;
            }
            int desiredAccessModifiers = originalClass.getDeclaredConstructor(
                asOriginalClassParams(ctConstr.getParameterTypes())).getModifiers();
            
            if (Modifier.isPrivate(desiredAccessModifiers)) {
                ctConstr.setModifiers(Modifier.setPrivate(ctModifiers));
            } else if (Modifier.isProtected(desiredAccessModifiers)) {
                ctConstr.setModifiers(Modifier.setProtected(ctModifiers));
            } else if (!Modifier.isPublic(desiredAccessModifiers)) {
                ctConstr.setModifiers(Modifier.setPackage(ctModifiers));
            } else {
                /* ctConstr remains public */
            }
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
    
    public static String signatureOf(Method m) {
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
    
    protected static String signatureOf(CtMethod m) throws NotFoundException {
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
                                             String testClass, String returnType, String methodName,
                                             String[] paramTypes) {
        StringBuilder builder = new StringBuilder(testClass)
                                    .append('\n').append(returnType)
                                    .append('\n').append(methodName);
        for (String param : paramTypes) {
            builder.append('\n').append(param);
        }
        return builder.toString();
    }
    
}
