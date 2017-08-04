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
package samples.powermockito.junit4.spy;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.spy.SpyObject;
import samples.suppressmethod.SuppressMethod;
import samples.suppressmethod.SuppressMethodParent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SpyObject.class, SuppressMethod.class, SuppressMethodParent.class})
public class SpyTest {

    private SpyObject partialMock = null;

    @Before
    public void setup() throws Exception {
        partialMock = spy(new SpyObject());
    }

    @Test
    public void should_stub_spying_on_private_method_works() throws Exception {
        when(partialMock, "getMyString").thenReturn("ikk2");

        assertThat(partialMock.getMyString(), equalTo("ikk2"));
        assertThat(partialMock.getStringTwo(), equalTo("two"));
    }
    
    @Test
    public void should_call_real_method_when_spy_method_is_not_stubbed() {
        Assertions.assertThat(partialMock.getMyString())
                  .as("Real method is called")
                  .isEqualTo(new SpyObject().getMyString());
    }
    

    @Test
    public void testSuppressMethodWhenObjectIsSpy() throws Exception {
        suppress(method(SuppressMethod.class, "myMethod"));

        SuppressMethod tested = spy(new SuppressMethod());
        assertEquals(0, tested.myMethod());
    }

    @Test
    public void testSuppressMethodInParentOnlyWhenObjectIsSpy() throws Exception {
        suppress(method(SuppressMethodParent.class, "myMethod"));

        SuppressMethod tested = spy(new SuppressMethod());
        assertEquals(20, tested.myMethod());
    }

    @Test
    public void testDoNothingForSpy() {
        doNothing().when(partialMock).throwException();

        partialMock.throwException();
    }
}