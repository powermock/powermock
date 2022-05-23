package samples.powermockito.junit4.bugs.github722;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.DisallowWriteToSystemErr;
import org.powermock.modules.junit4.internal.impl.DelegatingPowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class GitHub722Test {

    @Rule
    public final DisallowWriteToSystemErr disallowWriteToSystemErr = new DisallowWriteToSystemErr();

    @Test
    public void testDelegatingPowerMockRunnerUseTheories() throws Exception {

        String[] methodsToRun = {"testUseTheoriesTest"};
        DelegatingPowerMockRunner test = new DelegatingPowerMockRunner(UseTheoriesTest.class, methodsToRun, null);
        Method[] methods = Whitebox.getInternalState(test, "testMethods");
        String expected = "testUseTheoriesTest";
        int expectedSize = 1;
        assertThat(methods.length).describedAs("Check array size").isEqualTo(expectedSize);
        assertThat(methods[0].getName()).describedAs("Test using Theory annotation").isEqualTo(expected);
    }

    @Test
    public void testDelegatingPowerMockRunnerUseJUnit() throws Exception {

        String[] methodsToRun = {"testJUnitTest"};
        DelegatingPowerMockRunner test = new DelegatingPowerMockRunner(UseJUnitTest.class, methodsToRun, null);
        Method[] methods = Whitebox.getInternalState(test, "testMethods");
        String expected = "testJUnitTest";
        int expectedSize = 1;
        assertThat(methods.length).describedAs("Check array size").isEqualTo(expectedSize);
        assertThat(methods[0].getName()).describedAs("Test using Theory annotation").isEqualTo(expected);
    }
}
