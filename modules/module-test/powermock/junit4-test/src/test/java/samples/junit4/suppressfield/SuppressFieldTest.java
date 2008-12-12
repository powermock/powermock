package samples.junit4.suppressfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.suppressField;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.suppressfield.SuppressField;

/**
 * Unit tests that asserts that field suppression works.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressField.class)
public class SuppressFieldTest {

	@Test
	public void assertThatSpecificStaticFinalFieldSuppressionWorks() throws Exception {
		suppressField(Whitebox.getField(SuppressField.class, "MY_OBJECT"));
		assertNull(SuppressField.getMyObject());
	}

	@Ignore("Final primitive types doesn't work, see issue at http://code.google.com/p/powermock/issues/detail?id=85")
	@Test
	public void assertThatSpecificStaticFinalPrimitiveFieldSuppressionWorks() throws Exception {
		suppressField(Whitebox.getField(SuppressField.class, "MY_VALUE"));
		assertEquals(0, SuppressField.getMyValue());
	}

	@Ignore("Final primitive types doesn't work, see issue at http://code.google.com/p/powermock/issues/detail?id=85")
	@Test
	public void assertThatSpecificInstanceFinalPrimitiveFieldSuppressionWorks() throws Exception {
		suppressField(Whitebox.getField(SuppressField.class, "myBoolean"));
		SuppressField suppressField = new SuppressField();
		assertEquals(false, suppressField.isMyBoolean());
	}

	@Test
	public void assertThatSpecificInstanceFinalFieldSuppressionWorks() throws Exception {
		suppressField(Whitebox.getField(SuppressField.class, "myWrappedBoolean"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMyWrappedBoolean());
	}

	@Test
	public void assertThatSpecificPrimitiveInstanceFieldSuppressionWorks() throws Exception {
		suppressField(Whitebox.getField(SuppressField.class, "myChar"));
		SuppressField suppressField = new SuppressField();
		assertEquals(' ', suppressField.getMyChar());
	}

	@Test
	public void assertThatSpecificInstanceFieldSuppressionWorks() throws Exception {
		suppressField(Whitebox.getField(SuppressField.class, "mySecondValue"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
	}

	@Test
	public void assertThatMultipleInstanceFieldSuppressionWorks() throws Exception {
		suppressField(SuppressField.class, "mySecondValue", "myChar");
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
		assertEquals(' ', suppressField.getMyChar());
		assertEquals(Boolean.TRUE, suppressField.getMyWrappedBoolean());
	}

	// TODO Add final tests here as well when they work
	@Test
	public void assertThatAllFieldSuppressionWorks() throws Exception {
		suppressField(SuppressField.class);
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
		assertEquals(' ', suppressField.getMyChar());
		assertNull(suppressField.getMyWrappedBoolean());
		assertNull(SuppressField.getMyObject());
	}
}
