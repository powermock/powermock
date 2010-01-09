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
package samples.powermockito.junit4.staticandinstance;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.staticandinstance.StaticAndInstanceDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticAndInstanceDemo.class)
public class StaticAndInstanceDemoTest {

	@Test
	public void partialMockingOfStaticAndInstanceMethod() throws Exception {
		spy(StaticAndInstanceDemo.class);
		StaticAndInstanceDemo tested = spy(new StaticAndInstanceDemo());

		final String staticExpected = "a static message";
		when(StaticAndInstanceDemo.getStaticMessage()).thenReturn(staticExpected);
		final String privateExpected = "A private message ";
		when(tested, "getPrivateMessage").thenReturn(privateExpected);

		String actual = tested.getMessage();

		verifyStatic();	StaticAndInstanceDemo.getStaticMessage();
		verifyPrivate(tested).invoke("getPrivateMessage");
		assertEquals(privateExpected + staticExpected, actual);
	}
}
