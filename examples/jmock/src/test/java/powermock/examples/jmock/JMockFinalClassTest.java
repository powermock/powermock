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

/**
 * Demonstrates how to use PowerMock with unsupported frameworks like JMock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalClass.class)
public class JMockFinalClassTest {
    private Mockery context = new JUnit4Mockery(){{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private FinalClass tested;

    @Before
    public void setup() throws Exception {
        tested = context.mock(FinalClass.class);
    }

    @Test
    public void mockFinalClassWithPowerMockAndJMock() throws Exception {
        // Given
        final String expected = "something";
        context.checking(new Expectations(){{
            one(tested).helloWorld();
            will(returnValue(expected));
        }});

        // When
        final String actual = tested.helloWorld();
        assertEquals(expected, actual);

        // Then
        context.assertIsSatisfied();
    }
}
