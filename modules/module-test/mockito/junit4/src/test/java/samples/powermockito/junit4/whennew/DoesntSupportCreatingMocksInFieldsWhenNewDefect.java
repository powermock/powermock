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

package samples.powermockito.junit4.whennew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.newmocking.NewDemo;
import samples.newmocking.SomeDependency;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { 	NewDemo.class })
/*
  Issue <a href="298">http://code.google.com/p/powermock/issues/detail?id=298</a>
 */
public class DoesntSupportCreatingMocksInFieldsWhenNewDefect {

	final SomeDependency somethingUsedByMethodUnderTest=mock(SomeDependency.class); // for some reason the mocking only works if loadingPool is a local variable not a field (like all the other mocks)

	@Test
	public void methodUnderTestShouldWorkWithClassLevelMock() throws Exception {

		whenNew(SomeDependency.class).withNoArguments().thenReturn(somethingUsedByMethodUnderTest);

		NewDemo objectUnderTest = new NewDemo();
		
		objectUnderTest.methodUnderTest();
		
	}


	
}
