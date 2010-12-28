/*
 * Copyright 2010 the original author or authors.
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

package powermock.examples.jmock;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

/**
 * Demonstrates how to use PowerMock with unsupported frameworks like JMock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FinalClass.class, ClassWithStaticMethod.class})
public class JMockStaticMethodTest {
    private Mockery context = new JUnit4Mockery(){{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private FinalClass finalClassMock;
    private JMockExample tested;

    @Before
    public void setup() throws Exception {
        finalClassMock = context.mock(FinalClass.class);
        tested = new JMockExample(finalClassMock);
    }

    @Test
    public void mockFinalClassWithPowerMockAndJMock() throws Exception {
        // Given
        context.checking(new Expectations(){{
            one(finalClassMock).helloWorld();
            will(returnValue("Hello "));
        }});

        // Stub the static method
        stub(method(ClassWithStaticMethod.class, "returnString")).toReturn("JMock");

        // When
        final String message = tested.generateMessage();
        assertEquals("Message is: Hello JMock", message);

        // Then
        context.assertIsSatisfied();
    }
}
