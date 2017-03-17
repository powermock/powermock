package samples.powermockito.junit4.bugs.github733;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.internal.runners.TestClass;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(Enclosed.class)
public class UseTestAnnotatedTest extends TestClass {

    public UseTestAnnotatedTest(Class<MethodToTest> klass) {
        super(klass);
    }

    @RunWith(PowerMockRunner.class)
    public static class MethodToTest {

        @Test
        public void genericMethod() {
            // no prefix of test for method name
        }

    }


}
