package samples.junit4.misc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { PrivateInnerInterfacesInTestClassTest.class })
public class PrivateInnerInterfacesInTestClassTest {

	@Test
	public void privateInterfacesCanBeLoadedAndBytcodeManipulatedByPowerMock() throws Exception {
		InnerInterface innerInterface = new InnerInterface() {
			public String aMethod() {
				return "ok";
			}
		};
		assertEquals("ok", innerInterface.aMethod());
	}

	private interface InnerInterface {
		String aMethod();
	}
}
