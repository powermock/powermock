package samples.junit410.assume;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assume.assumeTrue;

@RunWith(PowerMockRunner.class)
public class AssumeForJUnit410Test {

    @Test
    public void assumesWorkWithPowerMockForJUnit410() throws Exception {
        // When
        assumeTrue(false);
    }
}
