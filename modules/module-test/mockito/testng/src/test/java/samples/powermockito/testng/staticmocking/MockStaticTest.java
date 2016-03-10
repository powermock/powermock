/*
 * Copyright 2008 the original author or authors.
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
package samples.powermockito.testng.staticmocking;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;
import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;


/**
 * Test class to demonstrate static, static+final, static+native and
 * static+final+native methods mocking.
 */
@PrepareForTest({StaticService.class, StaticHelper.class})
public class MockStaticTest extends PowerMockTestCase {

    @Test
    public void testMockStatic() throws Exception {
        mockStatic(StaticService.class);
        String expected = "Hello altered World";
        when(StaticService.say("hello")).thenReturn("Hello altered World");

        String actual = StaticService.say("hello");

        verifyStatic();
        StaticService.say("hello");

        assertEquals(expected, actual);
    }


    @Test
    public void testMockStaticFinal() throws Exception {
        mockStatic(StaticService.class);
        String expected = "Hello altered World";
        when(StaticService.sayFinal("hello")).thenReturn("Hello altered World");

        String actual = StaticService.sayFinal("hello");

        verifyStatic();
        StaticService.sayFinal("hello");

        assertEquals(expected, actual);
    }
}
