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
package samples.junit4.noannotation;

import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Demonstrates that the PowerMock JUnit runner works with single-arg string
 * constructor. Asserts that issue <a
 * href="http://code.google.com/p/powermock/issues/detail?id=174">174</a> is
 * fixed.
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { StaticService.class, StaticHelper.class })
public class StringConstructorWorksWhenExtendingTestCase extends TestCase {

    public StringConstructorWorksWhenExtendingTestCase(String name) {
        super(name);
    }

    public void testMockingStaticMethodWorksWhenStringArgConstructor() throws Exception {
        mockStatic(StaticService.class);
        String expected = "Hello altered World";
        expect(StaticService.say("hello")).andReturn("Hello altered World");
        replay(StaticService.class);

        String actual = StaticService.say("hello");

        verify(StaticService.class);
        assertEquals("Expected and actual did not match", expected, actual);

        // Singleton still be mocked by now.
        try {
            StaticService.say("world");
            fail("Should throw AssertionError!");
        } catch (AssertionError e) {
            assertEquals("\n  Unexpected method call StaticService.say(\"world\"):", e.getMessage());
        }
    }
}
