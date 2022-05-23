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
package samples.junit4.legacy.noannotation;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.legacy.PowerMockRunner;
import samples.staticandinstance.StaticAndInstanceDemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Test case that demonstrates the ability to run test cases not annotated with
 * {@link Test} when extending from {@link TestCase} using JUnit4 legacy
 * (4.0-4.3).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class NoAnnotationUsageTest extends TestCase {

	public void testShouldMockPrivateAndStaticMethod() throws Exception {
		mockStaticPartial(StaticAndInstanceDemo.class, "getStaticMessage");

		StaticAndInstanceDemo tested = createPartialMock(StaticAndInstanceDemo.class, "getPrivateMessage");

		final String staticExpected = "a static message";
		expect(StaticAndInstanceDemo.getStaticMessage()).andReturn(staticExpected);

		final String privateExpected = "A private message ";
		expectPrivate(tested, "getPrivateMessage").andReturn(privateExpected);

		replay(tested);
		replay(StaticAndInstanceDemo.class);

        assertThat(tested.getMessage()).isEqualTo(privateExpected + staticExpected);

        verify(tested);
        verify(StaticAndInstanceDemo.class);

	}

}
