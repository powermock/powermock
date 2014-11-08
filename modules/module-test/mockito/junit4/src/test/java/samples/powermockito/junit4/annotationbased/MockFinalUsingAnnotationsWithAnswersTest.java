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
package samples.powermockito.junit4.annotationbased;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test class to demonstrate non-static final mocking with Mockito and PowerMock
 * annotations using answers.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalDemo.class)
public class MockFinalUsingAnnotationsWithAnswersTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private FinalDemo tested1;

    @Mock(answer = Answers.RETURNS_MOCKS)
	private FinalDemo tested2;

    @Mock(answer = Answers.RETURNS_MOCKS, name = "myTested3")
	private FinalDemo tested3;

	@Test public void
    assert_mock_final_with_mockito_mock_annotation_with_deep_stubs_works() {
        when(tested1.simpleReturnExample().mySimpleMethod()).thenReturn(42);

        assertEquals(42, tested1.simpleReturnExample().mySimpleMethod());
	}

    @Test public void
    assert_mock_final_with_mockito_mock_annotation_with_returns_mocks_works() {
		assertNotNull(tested2.simpleReturnExample());
	}
}
