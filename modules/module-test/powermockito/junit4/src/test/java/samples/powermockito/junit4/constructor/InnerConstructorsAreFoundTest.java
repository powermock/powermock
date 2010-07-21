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
package samples.powermockito.junit4.constructor;

import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({ InnerConstructorsAreFoundTest.WorkingObjectUnderTesting.class,
		InnerConstructorsAreFoundTest.BuggyObjectUnderTesting.class,
		InnerConstructorsAreFoundTest.StillBuggyObjectUnderTesting.class })
@RunWith(PowerMockRunner.class)
public class InnerConstructorsAreFoundTest {

	interface AnyInterface {
	};

	class AnyImplementation implements AnyInterface {
		@Override
		public boolean equals(Object obj) {
			return true;
		}
	}

	class SomeOtherImplementationOfSomethingElse {
		@Override
		public boolean equals(Object obj) {
			return true;
		}
	};

	// <ObjectsUnderTesting>
	class WorkingObjectUnderTesting {
		void methodToTest() {
			new WorkingObjectToMock(new AnyImplementation());
		}
	}

	class BuggyObjectUnderTesting {
		void methodToTest() {
			new BuggyObjectToMock(new SomeOtherImplementationOfSomethingElse(), new AnyImplementation());
		}
	}

	class StillBuggyObjectUnderTesting {
		void methodToTest() {
			new StillBuggyObjectToMock(new SomeOtherImplementationOfSomethingElse(),
					new AnyInterface[] { new AnyImplementation() });
		}
	}

	// </ObjectsUnderTesting>

	// <ObjectsToMock>
	class WorkingObjectToMock {
		public WorkingObjectToMock(AnyInterface... anys) {
		}
	}

	class BuggyObjectToMock {
		@SuppressWarnings("unused")
		private final AnyInterface[] anys;

		public BuggyObjectToMock(SomeOtherImplementationOfSomethingElse other, AnyInterface... anys) {
			this.anys = anys;
		}
	}

	class StillBuggyObjectToMock {
		@SuppressWarnings("unused")
		private final AnyInterface[] anys;

		public StillBuggyObjectToMock(SomeOtherImplementationOfSomethingElse other, AnyInterface[] anys) {
			this.anys = anys;
		}
	}

	@Mock
	WorkingObjectToMock mockedWorkingObject;
	@Mock
	BuggyObjectToMock mockedBuggyObject;
	@Mock
	StillBuggyObjectToMock mockedStillBuggyObject;

	@Test
	public void shouldInjectNewInstance() throws Exception {
		whenNew(WorkingObjectToMock.class).withArguments(new AnyInterface[] { new AnyImplementation() }).thenReturn(
				mockedWorkingObject);

		new WorkingObjectUnderTesting().methodToTest();
	}

	@Test
	public void shouldInjectNewInstanceButFails() throws Exception {
		// this is the same as above and throws same exception
		whenNew(BuggyObjectToMock.class).withArguments(new SomeOtherImplementationOfSomethingElse(),
				(Object[]) new AnyInterface[] { new AnyImplementation() }).thenReturn(mockedBuggyObject);

		new BuggyObjectUnderTesting().methodToTest();
	}

	@Test
	public void shouldInjectNewInstanceButStillFails() throws Exception {
		whenNew(StillBuggyObjectToMock.class).withArguments(new SomeOtherImplementationOfSomethingElse(),
				(Object[]) new AnyInterface[] { new AnyImplementation() }).thenReturn(mockedStillBuggyObject);

		new StillBuggyObjectUnderTesting().methodToTest();
	}
}
