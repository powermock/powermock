package samples.powermockito.junit4.bugs.github733;

import org.junit.Test;
import org.powermock.modules.junit4.internal.impl.testcaseworkaround.PowerMockJUnit4MethodValidator;

public class GitHub733Test {

    @Test
    public void testPowerMockJUnit4MethodValidatorAcceptsTestAnnotatedMethods() throws Exception {
    
        PowerMockJUnit4MethodValidator validator = new PowerMockJUnit4MethodValidator(new UseTestAnnotatedTest(UseTestAnnotatedTest.MethodToTest.class));
        validator.validateInstanceMethods();
    }
}
