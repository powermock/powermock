package org.powermock.api.mockito.internal.mockcreation;

import org.junit.Test;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.util.MockUtil;
import org.mockito.mock.MockName;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MockCreatorTest {

	@Test
	public void should_return_mock_name_when_settings_have_name()
			throws NoSuchMethodException, SecurityException {
		final MockSettingsImpl<List<?>> settings = new MockSettingsImpl<List<?>>();
		settings.name("mylist");

		final List<?> result = DefaultMockCreator.mock(List.class, false, false, null,
		                                               settings, List.class.getMethod("add", Object.class));

		final MockName mockName = MockUtil.getMockName(result);
		assertNotNull(mockName);
		assertEquals("mylist", mockName.toString());
	}

	@Test
	public void should_return_class_name_when_settings_have_no_name()
			throws NoSuchMethodException, SecurityException {
		final MockSettingsImpl<List<?>> settings = new MockSettingsImpl<List<?>>();

		final List<?> result = DefaultMockCreator.mock(List.class, false, false, null,
		                                               settings, List.class.getMethod("add", Object.class));

		final MockName mockName = MockUtil.getMockName(result);
		assertNotNull(mockName);
		assertEquals("list", mockName.toString());
	}
}
