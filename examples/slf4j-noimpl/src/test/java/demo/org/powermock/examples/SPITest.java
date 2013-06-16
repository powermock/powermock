package demo.org.powermock.examples;

import static org.junit.Assert.*;

import demo.org.powermock.examples.SL4JUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.mockpolicies.Slf4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@MockPolicy(Slf4jMockPolicy.class)
public class SPITest {

	@Test
	public void powerMockCanLoadClassesThatHaveMethodsAndFieldsReturningClassesThatAreNotInClasspath() throws Exception {
		SL4JUser obj = new SL4JUser();
		assertTrue(obj.returnTrue());
	}

}
