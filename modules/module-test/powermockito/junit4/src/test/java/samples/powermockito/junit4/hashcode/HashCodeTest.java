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
package samples.powermockito.junit4.hashcode;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import javax.naming.InitialContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InitialContext.class)
public class HashCodeTest {
    private final static int EXPECTED_HASH = 12316;

    @Test
    public void shouldStubHashCode() throws Exception {
        stub(method(InitialContext.class, "hashCode")).andReturn(EXPECTED_HASH);
        final InitialContext context = new InitialContext();

        assertEquals(EXPECTED_HASH, context.hashCode());
    }

    @Test
    public void shouldMockHashCode() throws Exception {
        InitialContext context = mock(InitialContext.class);
        when(context.hashCode()).thenReturn(EXPECTED_HASH);

        assertEquals(EXPECTED_HASH, context.hashCode());
    }
}
