package samples.powermockito.junit4.bugs.github352;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 *
 */
public class GitHub352Test {


    @Test
    public void testCountShouldBe3WhenRunWithDefaultRunner() {
        JUnitCore jUnitCore = new JUnitCore();

        Result result = jUnitCore.run(MyTest.class);

        int testCount = result.getRunCount();
        assertThat(testCount).describedAs("Test count not match to expected.", 3).isEqualTo(3);
    }

    @Test
    public void testCountShouldBe3WhenRunWithPowerMockRunner() {
        JUnitCore jUnitCore = new JUnitCore();

        Request request = new Request() {
            @Override
            public Runner getRunner() {
                try {
                    return new PowerMockRunner(MyTest.class);
                } catch (Exception e) {
                    throw  new RuntimeException(e);
                }
            }
        };


        Result result = jUnitCore.run(request);

        int testCount = result.getRunCount();
        assertThat(testCount).describedAs("Test count not match to expected.", 3).isEqualTo(3);
    }
}
