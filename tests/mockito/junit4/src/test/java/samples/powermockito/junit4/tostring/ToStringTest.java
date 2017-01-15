/*
 * Copyright 2010 the original author or authors.
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
package samples.powermockito.junit4.tostring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.crypto.Cipher;

import static org.junit.Assert.assertNotNull;

/**
 * Test that demonstrates that <a
 * href="http://code.google.com/p/powermock/issues/detail?id=239">issue 239</a>
 * is resolved.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ToStringTest.class)
public class ToStringTest {
    private Cipher cipher;

    @Test
    public void toStringInvocationWorksInMockito() throws Exception {
        cipher = PowerMockito.mock(Cipher.class);

        assertNotNull(cipher.toString());
    }
}
