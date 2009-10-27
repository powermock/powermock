/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package powermock.classloading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.LinkedList;
import java.util.concurrent.Callable;

import javassist.CtClass;

import org.junit.Test;
import org.powermock.classloading.ClassloaderExecutor;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;

import powermock.classloading.classes.MyArgument;
import powermock.classloading.classes.MyClass;
import powermock.classloading.classes.MyIntegerHolder;
import powermock.classloading.classes.MyReturnValue;

public class ClassloaderExecutorTest {

    @Test
    public void classloaderExecutorLoadsObjectGraphInSpecifiedClassloaderAndReturnsResultInOriginalClassloader() throws Exception {
        MockClassLoader classloader = createClassloader();
        final MyReturnValue expectedConstructorValue = new MyReturnValue(new MyArgument("first value"));
        final MyClass myClass = new MyClass(expectedConstructorValue);
        final MyArgument expected = new MyArgument("A value");
        MyReturnValue[] actual = new ClassloaderExecutor(classloader).execute(new Callable<MyReturnValue[]>() {
            public MyReturnValue[] call() throws Exception {
                assertEquals(MockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                return myClass.myMethod(expected);
            }
        });

        assertFalse(MockClassLoader.class.getName().equals(this.getClass().getClassLoader().getClass().getName()));

        final MyReturnValue myReturnValue = actual[0];
        assertEquals(expectedConstructorValue.getMyArgument().getValue(), myReturnValue.getMyArgument().getValue());
        assertEquals(expected.getValue(), actual[1].getMyArgument().getValue());
    }

    @Test
    public void classloaderExecutorLoadsObjectGraphThatIncludesPrimitiveValuesInSpecifiedClassloaderAndReturnsResultInOriginalClassloader()
            throws Exception {
        MockClassLoader classloader = createClassloader();
        final Integer expected = 42;
        final MyIntegerHolder myClass = new MyIntegerHolder(expected);
        Integer actual = new ClassloaderExecutor(classloader).execute(new Callable<Integer>() {
            public Integer call() throws Exception {
                assertEquals(MockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                final int myInteger = myClass.getMyInteger();
                assertEquals((int) expected, myInteger);
                return myInteger;
            }
        });

        assertFalse(MockClassLoader.class.getName().equals(this.getClass().getClassLoader().getClass().getName()));

        assertEquals(expected, actual);
    }

    private MockClassLoader createClassloader() {
        MockClassLoader classloader = new MockClassLoader(new String[] { MyClass.class.getName(), MyArgument.class.getName(),
                MyReturnValue.class.getName() });
        MockTransformer mainMockTransformer = new MockTransformer() {
            public CtClass transform(CtClass clazz) throws Exception {
                return clazz;
            }
        };
        LinkedList<MockTransformer> linkedList = new LinkedList<MockTransformer>();
        linkedList.add(mainMockTransformer);
        classloader.setMockTransformerChain(linkedList);
        return classloader;
    }
}
