package samples.powermockito.junit4.doreturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * Created by gauee on 12/11/15.
 * Test that demonstrates that <a
 * href="https://github.com/jayway/powermock/issues/599">issue 599</a>
 * is resolved.
 */
@RunWith(PowerMockRunner.class)
public class DoReturnTest {

    private static final String TEMP_DAY_FIRST = "41F";
    private static final String TEMP_DAY_SECOND = "44F";

    @Mock
    private Weather weather;

    interface Weather {
        String getTemperature();
    }

    @Before
    public void init() {
        doReturn(TEMP_DAY_FIRST, TEMP_DAY_SECOND).when(weather).getTemperature();
    }

    @Test
    public void returnsDifferentTemperatureForEachInvocation(){
        assertThat(weather.getTemperature(), is(equalTo(TEMP_DAY_FIRST)));
        assertThat(weather.getTemperature(), is(equalTo(TEMP_DAY_SECOND)));
    }

    @Test
    public void returnsFirstTemperatureWhenPassedArrayIsEmpty() {
        doReturn(TEMP_DAY_FIRST, new Object[0]).when(weather).getTemperature();

        assertThat(weather.getTemperature(), is(equalTo(TEMP_DAY_FIRST)));
        assertThat(weather.getTemperature(), is(equalTo(TEMP_DAY_FIRST)));
    }

}
