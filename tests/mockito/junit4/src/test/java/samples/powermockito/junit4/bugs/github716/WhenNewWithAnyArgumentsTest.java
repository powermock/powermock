package samples.powermockito.junit4.bugs.github716;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({MyService.class})
@RunWith(PowerMockRunner.class)
public class WhenNewWithAnyArgumentsTest {

    @Mock
    private C c;

    @InjectMocks
    private MyService cut;

    @Test
    public void shouldStubNewConstructorCallIfOneOfActualParameterIsNull() throws Exception {
        A a = new A();

        whenNew(C.class).withAnyArguments().thenReturn(c);
        when(c.multiply()).thenReturn(42);

        int result = cut.doSomething(a, null);
        assertThat(result, is(42));
    }

}
