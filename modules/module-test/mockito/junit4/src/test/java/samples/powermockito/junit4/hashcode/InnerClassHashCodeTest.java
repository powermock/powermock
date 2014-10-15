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

package samples.powermockito.junit4.hashcode;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InnerClassHashCodeTest.SubHashMap.class})
public class InnerClassHashCodeTest {

    private static final int EXPECTED_HASH = 123456;

    @Test
    @Ignore("This should work but it's a bug")
    public void can_mock_inner_hash_code_method() {
        SubHashMap actor = mock(SubHashMap.class);
        when(actor.hashCode()).thenReturn(EXPECTED_HASH);

        int hashCode = actor.hashCode();

        assertThat(hashCode, equalTo(EXPECTED_HASH));
    }

    @Test
    public void can_stub_inner_hash_code_method() {
        stub(method(SubHashMap.class, "hashCode")).toReturn(EXPECTED_HASH);
        SubHashMap actor = new SubHashMap();

        int hashCode = actor.hashCode();

        assertThat(hashCode, equalTo(123456));
    }

    public class SubHashMap extends HashMap {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

}
