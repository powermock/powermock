/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package samples.junit4.largemethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.largemethod.MethodExceedingJvmLimit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.method;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MethodExceedingJvmLimit.class)
public class LargeMethodTest {

    @Test
    public void largeMethodShouldBeOverridden() {
        try {
            MethodExceedingJvmLimit.init();
            fail("Method should be overridden and exception should be thrown");
        } catch (Exception e) {
            assertSame(IllegalAccessException.class, e.getClass());
            assertTrue(e.getMessage().contains("Method was too large and after instrumentation exceeded JVM limit"));
        }
    }

    @Test
    public void largeMethodShouldBeAbleToBeSuppressed() {
        suppress(method(MethodExceedingJvmLimit.class, "init"));
        assertNull("Suppressed method should return: null", MethodExceedingJvmLimit.init());
    }

    @Test
    public void largeMethodShouldBeAbleToBeMocked() {
        mockStatic(MethodExceedingJvmLimit.class);
        expect(MethodExceedingJvmLimit.init()).andReturn("ok");
        replayAll();
        assertEquals("Mocked method should return: ok", "ok", MethodExceedingJvmLimit.init());
    }

    @Test(expected = IllegalStateException.class)
    public void largeMethodShouldBeAbleToBeMockedAndThrowException() {
        mockStatic(MethodExceedingJvmLimit.class);
        expect(MethodExceedingJvmLimit.init()).andThrow(new IllegalStateException());
        replayAll();
        MethodExceedingJvmLimit.init();
    }
}
