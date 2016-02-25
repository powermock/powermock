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

package powermock.modules.test.mockito.junit4.agent;

import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.Service;
import samples.annotationbased.AnnotationDemo;

import java.util.Arrays;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(Parameterized.class)
public class AnnotationUsageTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();


    @TestSubject
    private final
    AnnotationDemo tested = new AnnotationDemo();


    @SuppressWarnings("unused")
    @Mock
    private Service server;

    private final String fooId;


    public AnnotationUsageTest(String fooId) {
        this.fooId = fooId;
    }


    @Parameterized.Parameters()
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"1"},
                {"2"}
        });
    }


    @Before
    public void setUp() {
        expect(server.getServiceMessage()).andReturn(fooId);
        replay(server);
    }


    @Test
    public void annotationsAreEnabledWhenUsingTheJUnitRule() {
        String serviceMessage = tested.getServiceMessage();
        assertEquals(fooId, serviceMessage);
    }
}
