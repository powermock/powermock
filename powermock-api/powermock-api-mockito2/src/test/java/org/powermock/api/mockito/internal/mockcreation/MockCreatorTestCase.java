package org.powermock.api.mockito.internal.mockcreation;

import org.junit.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.mock.MockName;
import org.powermock.api.mockito.invocation.MockitoMethodInvocationControl;
import org.powermock.core.MockRepository;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MockCreatorTestCase {

	@Test
	public void should_return_mock_name_when_settings_have_name() throws NoSuchMethodException, SecurityException {
        final String definedMockName = "my-list";
        final MockSettings settings = Mockito.withSettings().name(definedMockName);
        
        final List<?> result = createMock(settings);
        
        final MockName mockName = getMockName(result);
        
        assertThat(mockName.toString())
            .as("Mock name is configured")
            .isEqualTo(definedMockName);
    }
    
    @Test
    public void should_return_class_name_when_settings_have_no_name() throws NoSuchMethodException, SecurityException {
        final MockSettings settings = Mockito.withSettings();
        
        final List<?> result = createMock(settings);
        
        final MockName mockName = getMockName(result);
        
        assertThat(mockName.toString())
            .as("Mock name is configured")
            .isEqualTo("list");
    }
    
    private List<?> createMock(final MockSettings settings) throws NoSuchMethodException {
        return DefaultMockCreator.mock(List.class, false, false, null, settings, List.class.getMethod("add", Object.class));
    }
    
    private MockName getMockName(final List<?> result) {
        final MockitoMethodInvocationControl invocationControl = (MockitoMethodInvocationControl) MockRepository.getInstanceMethodInvocationControl(result);
        return invocationControl.getMockHandlerAdaptor().getMockSettings().getMockName();
    }
}
