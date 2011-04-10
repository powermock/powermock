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

package org.powermock.examples.spring.mockito;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import powermock.examples.spring.IdGenerator;
import powermock.examples.spring.Message;
import powermock.examples.spring.MyBean;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/example-context.xml")
@PrepareForTest(IdGenerator.class)
public class SpringExampleTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Autowired
    private MyBean myBean;

    @Test
    public void mockStaticMethod() throws Exception {
        // Given
        final long expectedId = 2L;
        mockStatic(IdGenerator.class);
        when(IdGenerator.generateNewId()).thenReturn(expectedId);

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(expectedId, message.getId());
        assertEquals("Tom Cruise, Paul Enderson, George Bush", message.getContent());
    }

    @Test
    public void mockStaticMethodAndVerify() throws Exception {
        // Given
        final long expectedId = 2L;
        mockStatic(IdGenerator.class);
        when(IdGenerator.generateNewId()).thenReturn(expectedId);

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(expectedId, message.getId());
        assertEquals("Tom Cruise, Paul Enderson, George Bush", message.getContent());
        verifyStatic(); IdGenerator.generateNewId();
    }

    @Test
    public void stubStaticMethod() throws Exception {
        // Given
        final long expectedId = 2L;
        stub(method(IdGenerator.class, "generateNewId")).toReturn(expectedId);

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(expectedId, message.getId());
        assertEquals("Tom Cruise, Paul Enderson, George Bush", message.getContent());
    }

    @Test
    public void suppressStaticMethod() throws Exception {
        // Given
        suppress(method(IdGenerator.class, "generateNewId"));

        // When
        final Message message = myBean.generateMessage();

        // Then
        assertEquals(0L, message.getId());
        assertEquals("Tom Cruise, Paul Enderson, George Bush", message.getContent());
    }

}
