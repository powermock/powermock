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
package samples.powermockito.junit4.membermodification;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberMatcher.constructorsDeclaredIn;
import static org.powermock.api.support.membermodification.MemberMatcher.everythingDeclaredIn;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberMatcher.methods;
import static org.powermock.api.support.membermodification.MemberMatcher.methodsDeclaredIn;
import static org.powermock.api.support.membermodification.MemberModifier.replace;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.staticandinstance.StaticAndInstanceDemo;
import samples.suppressconstructor.SuppressConstructorHierarchy;
import samples.suppresseverything.SuppressEverything;
import samples.suppressfield.SuppressField;
import samples.suppressmethod.SuppressMethod;

/**
 * Demonstrates PowerMock's ability to modify member structures.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { SuppressMethod.class, SuppressField.class, SuppressEverything.class })
public class MemberModificationExampleTest {

    @Test
    public void suppressSingleMethodExample() throws Exception {
        suppress(method(SuppressMethod.class, "getObject"));

        assertNull(new SuppressMethod().getObject());
    }

    @Test
    public void suppressMultipleMethodsExample1() throws Exception {
        suppress(methods(SuppressMethod.class, "getObject", "getInt"));

        assertNull(new SuppressMethod().getObject());
        assertEquals(0, new SuppressMethod().getInt());
    }

    @Test
    public void suppressMultipleMethodsExample2() throws Exception {
        suppress(methods(method(SuppressMethod.class, "getObject"), method(SuppressMethod.class, "getInt")));

        assertNull(new SuppressMethod().getObject());
        assertEquals(0, new SuppressMethod().getInt());
    }

    @Test
    public void suppressAllMethodsExample() throws Exception {
        suppress(methodsDeclaredIn(SuppressMethod.class));

        final SuppressMethod tested = new SuppressMethod();

        assertNull(tested.getObject());
        assertNull(SuppressMethod.getObjectStatic());
        assertEquals(0, tested.getByte());
    }

    @Test
    public void suppressSingleFieldExample() throws Exception {
        suppress(field(SuppressField.class, "domainObject"));

        SuppressField tested = new SuppressField();
        assertNull(tested.getDomainObject());
    }

    @Test
    public void suppressConstructorExample() throws Exception {
        suppress(constructor(SuppressConstructorHierarchy.class));

        SuppressConstructorHierarchy tested = new SuppressConstructorHierarchy("message");

        assertEquals(42, tested.getNumber());
        assertNull(tested.getMessage());
    }

    @Test
    public void stubSingleMethodExample() throws Exception {
        final String expectedReturnValue = "new";
        stub(method(SuppressMethod.class, "getObject")).toReturn(expectedReturnValue);

        final SuppressMethod tested = new SuppressMethod();
        assertEquals(expectedReturnValue, tested.getObject());
        assertEquals(expectedReturnValue, tested.getObject());
    }

    @Test
    public void duckTypeStaticMethodExample() throws Exception {
        replace(method(SuppressMethod.class, "getObjectStatic")).with(
                method(StaticAndInstanceDemo.class, "getStaticMessage"));

        assertEquals(SuppressMethod.getObjectStatic(), StaticAndInstanceDemo.getStaticMessage());
    }

    @Test
    public void whenReplacingMethodWithAMethodOfIncorrectReturnTypeThenAnIAEIsThrown() throws Exception {
        try {
            replace(method(SuppressMethod.class, "getObjectStatic")).with(
                    method(StaticAndInstanceDemo.class, "aVoidMethod"));
            fail("Should thow IAE");
        } catch (Exception e) {
            assertEquals("The replacing method (public static void samples.staticandinstance.StaticAndInstanceDemo.aVoidMethod()) needs to return java.lang.Object and not void.", e.getMessage());
        }
    }

    @Test
    public void whenReplacingMethodWithAMethodOfWithIncorrectParametersThenAnIAEIsThrown() throws Exception {
        try {
            replace(method(SuppressMethod.class, "getObjectStatic")).with(
                    method(StaticAndInstanceDemo.class, "aMethod2"));
            fail("Should thow IAE");
        } catch (Exception e) {
            assertEquals("The replacing method, \"public static java.lang.Object samples.staticandinstance.StaticAndInstanceDemo.aMethod2(java.lang.String)\", needs to have the same number of parameters of the same type as as method \"public static java.lang.Object samples.suppressmethod.SuppressMethod.getObjectStatic()\".", e.getMessage());
        }
    }

    @Test
    public void changingReturnValueExample() throws Exception {
        replace(method(SuppressMethod.class, "getObjectWithArgument")).with(new ReturnValueChangingInvocationHandler());

        final SuppressMethod tested = new SuppressMethod();

        assertThat(tested.getObjectWithArgument("don't do anything"), is(instanceOf(Object.class)));
        assertEquals("hello world", tested.getObjectWithArgument("make it a string"));
    }

    @Test
    public void suppressAllConstructors() throws Exception {
        suppress(constructorsDeclaredIn(SuppressEverything.class));

        SuppressEverything suppressEverything = new SuppressEverything();
        new SuppressEverything("test");

        try {
            suppressEverything.something();
            fail("Should throw ISE");
        } catch (IllegalStateException e) {
            assertEquals("error", e.getMessage());
        }
    }

    @Test
    public void suppressEverythingExample() throws Exception {
        suppress(everythingDeclaredIn(SuppressEverything.class));

        SuppressEverything suppressEverything = new SuppressEverything();
        new SuppressEverything("test");
        suppressEverything.something();
        suppressEverything.somethingElse();
    }

    private final class ReturnValueChangingInvocationHandler implements InvocationHandler {
        public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
            if (arguments[0].equals("make it a string")) {
                return "hello world";
            } else {
                return method.invoke(object, arguments);
            }
        }
    }
}
