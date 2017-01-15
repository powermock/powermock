package samples.powermockito.junit4.bugs.github701;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MapWrapper.class})
public class GitHub701Test {

    private MapWrapper mocked;

    @Before
    public void setUp() throws Exception {
        mocked = mock(MapWrapper.class);
    }

    @Test
    public void shouldMockObjectAndReturnRequiredResult() throws Exception {

        doReturn("1234").when(mocked).get("numbers");

        assertThat(mocked.get("numbers")).isEqualTo("1234");
    }

    @Test
    public void shouldMockEqualsMethod() {

        MapWrapper mapWrapper = new MapWrapper();
        doReturn(true).when(mocked).equals(mapWrapper);

        assertThat(mocked.equals(mapWrapper)).isEqualTo(true);
    }

}
