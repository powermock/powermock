package samples.powermockito.junit4.bugs.github668;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.security.auth.Subject;

import java.util.HashSet;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Subject.class)
public class GitHub668Test {

    @Test
    public void shouldMockJavaxSystemFinalClasses() {
        Subject subject = mock(Subject.class);

        final HashSet<Object> value = new HashSet<Object>();
        when(subject.getPrivateCredentials()).thenReturn(value);

        assertThat(subject.getPrivateCredentials()).isSameAs(value);
    }

}
