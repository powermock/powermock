/*
 * Copyright 2014 the original author or authors.
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
package powermock.modules.test.mockito.junit4.delegate.parameterized;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import samples.suppressconstructor.InvokeConstructor;
import samples.suppressconstructor.SuppressConstructorHierarchy;

import static org.junit.Assert.*;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(SuppressConstructorHierarchy.class)
public class SuppressConstructorHierarchyDemoTest {

    @Parameterized.Parameter
    public boolean suppress;

    @Parameterized.Parameters(name = "suppress={0}")
    public static List<?> false_or_true() {
        return Arrays.asList(new Object[][]{{false}, {true}});
    }

    @Before
    public void suppressOnDemand() {
        if (suppress) {
            suppress(constructor(SuppressConstructorHierarchy.class));
        }
    }

    @Test
    public void directConstructorUsage() throws Exception {
        System.out.println("ClassLoader: " + getClass().getClassLoader());
        try {
            SuppressConstructorHierarchy tested
                    = new SuppressConstructorHierarchy("message");
            if (suppress) {
                assertNull(
                        "Message should have been null since we're skipping the execution of the constructor code. Message was \"message\".",
                        tested.getMessage());
                assertEquals("getNumber() value", 42, tested.getNumber());
            } else {
                fail("Expected RuntimeException");
            }
        } catch (RuntimeException ex) {
            if (suppress) {
                throw ex;
            } else {
                assertEquals("This should be suppressed!!", ex.getMessage());
            }
        }
    }

    @Test
    public void useConstructorInvoker() throws Exception {
        System.out.println("ClassLoader: " + getClass().getClassLoader());
        try {
            final String message = new InvokeConstructor().doStuff("qwe");
            if (suppress) {
                assertNull("Message should have been null since we're skipping the execution of the constructor code. Message was \"" + message + "\".",
                        message);
            } else {
                fail("Expected RuntimeException");
            }
        } catch (RuntimeException ex) {
            if (suppress) {
                throw ex;
            } else {
                assertEquals("This should be suppressed!!", ex.getMessage());
            }
        }
    }

    @Test
    @PrepareForTest
    public void suppressWithoutByteCodeManipulation() throws Exception {
        System.out.println("ClassLoader: " + getClass().getClassLoader());
        try {
            new InvokeConstructor().doStuff("qwe");
            fail("Should throw RuntimeException since we're running this test with a new class loader!");
        } catch (RuntimeException ex) {
            assertEquals("This should be suppressed!!", ex.getMessage());
        }
    }
}
