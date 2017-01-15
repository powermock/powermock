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

package samples.junit410.rules;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.junit410.rules.impl.SimpleEasyMockJUnitRule;
import samples.rule.SimpleThingCreator;
import samples.rule.SimpleThingImpl;
import samples.rule.ThingToTest;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimpleThingCreator.class)
public class ExceptionHandlingRuleTest {

    @Rule
    public SimpleEasyMockJUnitRule mocks = new SimpleEasyMockJUnitRule();

    private SimpleThingImpl simpleThingMock = mocks.createMock(SimpleThingImpl.class);

    // object under test
    private ThingToTest testThing;

    @Before
    public void setUp() throws Exception {
        mockStatic(SimpleThingCreator.class);
        expect(SimpleThingCreator.createSimpleThing()).andReturn(simpleThingMock);
        replay(SimpleThingCreator.class);

        verify(SimpleThingCreator.class);
    }

    @Test
    @Ignore("This test SHOULD fail but how do we expect it when verification happens in the rule?")
    public void exceptionThrownByRuleFailsTheTest() throws Exception {
        final String expectedName = "Smith";
        expect(simpleThingMock.getThingName()).andReturn(expectedName);
        mocks.replay();

        assertEquals("wrong name", expectedName, testThing.getName());
        // verify will be called by rule
    }
}
