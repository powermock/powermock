package samples.powermockito.junit4.bugs.github722;

import org.junit.runner.RunWith;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Theories.class)
public class UseTheoriesTest {

    @Theory
    public void testUseTheoriesTest() {
        //some test code here
    }
}
