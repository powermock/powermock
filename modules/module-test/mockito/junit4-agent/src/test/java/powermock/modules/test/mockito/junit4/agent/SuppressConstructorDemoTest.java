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
package powermock.modules.test.mockito.junit4.agent;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.suppressconstructor.SuppressConstructorDemo;
import samples.suppressconstructor.SuppressConstructorSubclassDemo;

import static org.junit.Assert.assertNull;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

/**
 * This test demonstrates how to tell PowerMock to avoid executing constructor
 * code for a certain class. This is crucial in certain tests where the
 * constructor or a subclass's constructor performs operations that are of no
 * concern to the unit test of the actual class or if the constructor performs
 * operations, such as getting services from a runtime environment that has not
 * been initialized. In normal situations you're forced to create an integration
 * or function test for the class instead (and thus the runtime environment is
 * available). This is not particularly good when it comes to testing method
 * logic.
 */
@PrepareForTest(SuppressConstructorDemo.class)
public class SuppressConstructorDemoTest {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * This test makes sure that the real constructor has never been called.
     */
    @Test
    public void testSuppressConstructor() throws Exception {
        suppress(constructor(SuppressConstructorDemo.class));
        final SuppressConstructorDemo tested = new SuppressConstructorDemo("a message");
        assertNull("Message should have been null since we're skipping the execution of the constructor code.", tested.getMessage());
    }

    /**
     * This test makes sure that the real parent constructor has never been
     * called.
     */
    @Test
    @Ignore("Doesn't work when using the Java agent")
    public void testSuppressParentConstructor() throws Exception {
        suppress(constructor(SuppressConstructorSubclassDemo.class));
        final SuppressConstructorDemo tested = new SuppressConstructorDemo("a message");
        assertNull("Message should have been null since we're skipping the execution of the constructor code.", tested.getMessage());
    }
}
