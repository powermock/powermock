package org.powermock.api.mockito.internal.mockcreation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.util.MockUtil;
import org.mockito.mock.MockName;

public class MockCreatorTest {

	private final MockUtil util = new MockUtil();

	@Test
	public void testMock_shouldReturnMockNameWhenSettingsHaveName()
			throws NoSuchMethodException, SecurityException {
		final MockSettingsImpl<List<?>> settings = new MockSettingsImpl<List<?>>();
		settings.name("mylist");

		final List<?> result = MockCreator.mock(List.class, false, false, null,
				settings, List.class.getMethod("add", Object.class));

		final MockName mockName = util.getMockName(result);
		assertNotNull(mockName);
		assertEquals("mylist", mockName.toString());
	}

	@Test
	public void testMock_shouldReturnClassNameWhenSettingsHaveNoName()
			throws NoSuchMethodException, SecurityException {
		final MockSettingsImpl<List<?>> settings = new MockSettingsImpl<List<?>>();

		final List<?> result = MockCreator.mock(List.class, false, false, null,
				settings, List.class.getMethod("add", Object.class));

		final MockName mockName = util.getMockName(result);
		assertNotNull(mockName);
		assertEquals("list", mockName.toString());
	}
}
