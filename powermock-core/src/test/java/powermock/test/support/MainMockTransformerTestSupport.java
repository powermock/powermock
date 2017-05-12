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

import java.util.LinkedList;
import java.util.List;

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
    
    public static class CallSpy {
        public static void registerMethodCall(String methodName) {
            methodCalls.add(methodName);
        }
    
        public static void registerFieldCall(String fieldName) {
            fieldCalls.add(fieldName);
        }
        
        public static List<String> getMethodCalls() {
            return methodCalls;
        }
        
        public static List<String> getFieldCalls() {
            return fieldCalls;
        }
    
        private final static List<String> methodCalls = new LinkedList<String>();
        private final static List<String> fieldCalls = new LinkedList<String>();
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
}