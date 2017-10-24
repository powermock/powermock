/*
 * Copyright 2011 the original author or authors.
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

package powermock.test.support;

import org.powermock.core.classloader.MockClassLoaderConfiguration;
import powermock.test.support.MainMockTransformerTestSupport.ConstructorCall.SupperClassThrowsException;

/**
 * This class is used when running tests for different {@link org.powermock.core.transformers.MockTransformer}. It is
 * placed in this package because classes in org.powermock.core.* are deferred by:
 * {@link MockClassLoaderConfiguration#getDeferPackages()}.
 * Additionally, the class must be modified when it is loaded, and as such not in {@link MockClassLoaderConfiguration#shouldLoadWithMockClassloaderWithoutModifications(String)}.
 */
public class MainMockTransformerTestSupport {
    
    public static class StaticInitialization {
        public static String value;
        
        static {
            value = "Init";
        }
    }
    
    public interface SomeInterface {
    
    }
    
    public interface ParameterInterface {
    
    }
    
    public static class ConstructorCall {
        
        public static class SupperClassThrowsException {
    
            public static final String MESSAGE = "Constructor of super class has been called";
    
            public SupperClassThrowsException(String name, double value) {
                throw new IllegalArgumentException(MESSAGE);
            }
    
            public SupperClassThrowsException(final ParameterImpl value) {
                throw new IllegalArgumentException(MESSAGE);
            }
    
            public SupperClassThrowsException(final long... array) {
                throw new IllegalArgumentException(MESSAGE);
            }
    
            public void aVoid() {
        
            }
    
            public void bVoid(final String lname, final long lvalue) {
        
            }
        }
    
    }
    
    public static class SuperClassWithObjectMethod {
    
        public static boolean syntheticMethodIsCalled = false;
        
        public void doSomething(Object o) {
            
        }
    }
    
    public static class SubclassWithBridgeMethod extends SuperClassWithObjectMethod {
        public void doSomething(String s) {
            
        }
    }
    
    public static class SupportClasses {
        
        private static final Object finalStaticField = new Object();
        private static transient final Object finalStaticTransientField = new Object();
        private final Object finalField = new Object();
        
        public enum EnumClass {
            VALUE
        }
        
        public final static class StaticFinalInnerClass {
        }
        
        public final static class FinalInnerClass {
        }
        
        private final static class PrivateStaticFinalInnerClass {
        }
        
        public static class MultipleConstructors {
            
            public MultipleConstructors() {}
            
            protected MultipleConstructors(String s) {}
            
            MultipleConstructors(int i) {}
            
            private MultipleConstructors(Boolean[] array) {}
            
            protected MultipleConstructors(int[] iarray, boolean b, String[] sarray) {}
        }
        
        public static class PublicSuperClass {
    
            public PublicSuperClass(String name) {
            
            }
            
        }
        
        
        class SuperClass {
        }
        
        public class SubClass extends SuperClass {
            public void dummyMethod() {}
        }
    }
    
    public static class SuperClassCallSuperConstructor extends SupperClassThrowsException {
        private final String field;
    
        public SuperClassCallSuperConstructor(final String name, String field, final double value) {
            super(name, value);
            this.field = field;
        
            String lname = name + "(a)";
            long lvalue = (long) value * 2;
        
            if (lname == null) {
                aVoid();
            } else {
                bVoid(lname, lvalue);
            }
            cVoid(lname, lvalue);
        }
    
        private void cVoid(final String lname, final long lvalue) {
        
        }
    }
    
    public static class SuperClassCallSuperConstructorWithCast extends SupperClassThrowsException {
        
        public SuperClassCallSuperConstructorWithCast(final ParameterInterface value) {
            super((ParameterImpl) value);
        }
    }
    
    public static class SuperClassCallSuperConstructorWithVararg extends SupperClassThrowsException {
        
        public SuperClassCallSuperConstructorWithVararg(final long... array) {
            super(array);
        }
    }
    
    public static class ParameterImpl implements ParameterInterface {
    
    
    }
    
    public static class StaticVoidMethodsTestClass {
        
        private static Object field;
        
        private static String lname;
        private static long value;
        
        public static void voidMethod(final String name, String field, final double value)
        
        {
            String lname = name + "(a)";
            long lvalue = (long) value * 2;
            
            if (name == null) {
                aVoid(lvalue);
            } else {
                bVoid(lname);
            }
            
            StaticVoidMethodsTestClass.field = field;
        }
        
        private static void bVoid(final String lname) {
            StaticVoidMethodsTestClass.lname = lname;
        }
        
        private static void aVoid(final long value) {
            StaticVoidMethodsTestClass.value = value;
        }
    }
    
    public static class VoidMethodsTestClass {
        
        private Object field;
        
        private String lname;
        private long value;
        
        public void voidMethod(final String name, String field, final double value) {
            String lname = name + "(a)";
            long lvalue = (long) value * 2;
            
            if (name == null) {
                aVoid(lvalue);
            } else {
                voidPrivateMethod(lname);
            }
            
            this.field = field;
        }
        
        public void finalVoidMethod(final String name, String field, final double value) {
            String lname = name + "(a)";
            long lvalue = (long) value * 2;
            
            if (name == null) {
                aVoid(lvalue);
            } else {
                voidPrivateMethod(lname);
            }
            
            this.field = field;
        }
    
        private void voidPrivateMethod(final String lname) {
            this.lname = lname;
        }
        
        private void aVoid(final long value) {
            this.value = value;
        }
    }
    
    public static class ReturnMethodsTestClass {
        
        private String lname;
        private long value;
        
        public String returnMethod(final String name, String field, final double value) {
            long lvalue = (long) value * 2;
    
            if (name == null) {
                return aVoid(lvalue);
            } else {
                return privateReturnMethod(name);
            }
        }
        
        public final String finalReturnMethod(final String name, String field, final double value) {
            long lvalue = (long) value * 2;
            
            if (name == null) {
                return aVoid(lvalue);
            } else {
                return privateReturnMethod(name);
            }
        }
    
        private String privateReturnMethod(final String name) {
            final String lname = name + "(a)";
            this.lname = lname;
            return lname;
        }
    
        private String aVoid(final long value) {
            this.value = value;
            return "" + value;
        }
    }
    
    public static abstract class AbstractMethodTestClass {
        public abstract String returnMethod(final String name, String field, final double value);
    }
    
    public static class NativeMethodsTestClass {
        public static native String nativeStaticReturnMethod(final String name);
        public native String nativeReturnMethod(final String name);
    }
}