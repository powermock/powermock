package samples.powermockito.junit4.bugs.github704;

import org.eclipse.core.runtime.FileLocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SomeClassUseSignedClasses.class, FileLocator.class})
public class PrepareForTestSignedTest {

    @Test
    public void shouldBeAbleMockSignedClasses(){
        FileLocator fileLocator = mock(FileLocator.class);

        mockStatic(SomeClassUseSignedClasses.class);

        when(SomeClassUseSignedClasses.getFileLocator()).thenReturn(fileLocator);

        assertThat(SomeClassUseSignedClasses.getFileLocator()).isNotNull();
    }
}
