package samples.junit4.mockpolicy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.mockpolicy.ResultCalculator;
import samples.mockpolicy.SomeClassWithAMethod;

/**
 * A simple example of a mock policy that stubs out a method call.
 */
@RunWith(PowerMockRunner.class)
@MockPolicy(MockPolicyExample.class)
public class MockPolicyUsageExampleTest {

	@Test
	public void exampleOfStubbingOutCallsInParentClass() throws Exception {
		SomeClassWithAMethod tested = new SomeClassWithAMethod();
		assertEquals(8.0d, tested.getResult(), 0.0d);
	}
}

class MockPolicyExample implements PowerMockPolicy {
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(ResultCalculator.class.getName());
	}

	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		Method calculateMethod = Whitebox.getMethod(ResultCalculator.class);
		settings.addSubtituteReturnValue(calculateMethod, 4);
	}
}
