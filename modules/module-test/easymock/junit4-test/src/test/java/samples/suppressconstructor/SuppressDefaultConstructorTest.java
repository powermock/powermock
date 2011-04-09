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

package samples.suppressconstructor;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.support.membermodification.MemberMatcher.constructor;
import static org.powermock.api.support.membermodification.MemberMatcher.defaultConstructorIn;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppaleList.class)
public class SuppressDefaultConstructorTest {

	@Test
	public void powerMockCanSuppressDefaultConstructor() {
		suppress(defaultConstructorIn(AppaleList.class));
		AppaleList appaleList = new AppaleList();
		String str = appaleList.getAll();
		assertEquals(str, "str");
	}

	@Test(expected = IllegalArgumentException.class)
	public void powerMockSuppressesOnlyDefaultConstructorWhenUsingDefaultConstructorIn() {
		suppress(defaultConstructorIn(AppaleList.class));
		new AppaleList("something");
	}

    @Test
	public void powerMockCanSuppressDefaultConstructorUsingConstructorMethod() {
		suppress(constructor(AppaleList.class, new Class[0]));
		AppaleList appaleList = new AppaleList();
		String str = appaleList.getAll();
		assertEquals(str, "str");
	}

    @Test(expected = IllegalArgumentException.class)
	public void powerMockSuppressesOnlyDefaultConstructorWhenApplicable() {
		suppress(constructor(AppaleList.class, new Class[0]));
		new AppaleList("something");
	}
}
