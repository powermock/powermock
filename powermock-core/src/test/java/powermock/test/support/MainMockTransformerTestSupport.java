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

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.impl.ClassMockTransformerTest;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used when running tests in {@link ClassMockTransformerTest}. It is
 * placed in this package because classes in org.powermock.core.* are deferred by:
 * {@link MockClassLoader#packagesToBeDeferred}. Additionally, the class must be modified when it is loaded, and as such
 * not in {@link MockClassLoader#packagesToLoadButNotModify}.
 */
public class MainMockTransformerTestSupport {
    public static class SupportClasses {
        public final static class StaticFinalInnerClass {
        }

        public final static class FinalInnerClass {
        }


        private final static class PrivateStaticFinalInnerClass {
        }

        public enum EnumClass {
            VALUE
        }

        class SuperClass {
        }

        public class SubClass extends SuperClass {
            public void dummyMethod() {}
        }

        public static class MultipleConstructors {

            public MultipleConstructors() {}
            protected MultipleConstructors(String s) {}
            MultipleConstructors(int i) {}
            private MultipleConstructors(Boolean[] array) {}
            protected MultipleConstructors(int[] iarray, boolean b, String[] sarray) {}
        }
    }

    public static class SuperClassWithObjectMethod {
        public void doSomething(Object o){

        }
    }

    public static class SubclassWithBridgeMethod extends SuperClassWithObjectMethod{
        public void doSomething(String s){

        }
    }

    public static class CallSpy{
        private final static List<String> methodCalls = new LinkedList<String>();
        private final static List<String> fieldCalls = new LinkedList<String>();

        public static void registerMethodCall(String methodName){
            methodCalls.add(methodName);
        }

        public static void registerFieldCall(String fieldName){
            fieldCalls.add(fieldName);
        }

        public static List<String> getMethodCalls() {
            return methodCalls;
        }

        public static List<String> getFieldCalls() {
            return fieldCalls;
        }
    }
}
