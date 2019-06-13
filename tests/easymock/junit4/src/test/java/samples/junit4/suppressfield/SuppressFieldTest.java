package samples.junit4.suppressfield;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.suppressfield.SuppressField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

/**
 * Unit tests that asserts that field suppression works.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SuppressField.class)
public class SuppressFieldTest {

	@Test
	public void assertThatSpecificStaticFinalFieldSuppressionWorks() throws Exception {
		suppress(field(SuppressField.class, "MY_OBJECT"));
		assertNull(SuppressField.getMyObject());
	}

	@Ignore("Final primitive types doesn't work, see issue at https://github.com/jayway/powermock/issues/105")
	@Test
	public void assertThatSpecificStaticFinalPrimitiveFieldSuppressionWorks() throws Exception {
		suppress(field(SuppressField.class, "MY_VALUE"));
		assertEquals(0, SuppressField.getMyValue());
	}

	@Ignore("Final primitive types doesn't work, see issue at https://github.com/jayway/powermock/issues/105")
	@Test
	public void assertThatSpecificInstanceFinalPrimitiveFieldSuppressionWorks() throws Exception {
		suppress(field(SuppressField.class, "myBoolean"));
		SuppressField suppressField = new SuppressField();
		assertEquals(false, suppressField.isMyBoolean());
	}

	@Test
	public void assertThatSpecificInstanceFinalFieldSuppressionWorks() throws Exception {
		suppress(field(SuppressField.class, "myWrappedBoolean"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMyWrappedBoolean());
	}

	@Test
	public void assertThatSpecificPrimitiveInstanceFieldSuppressionWorks() throws Exception {
		suppress(field(SuppressField.class, "myChar"));
		SuppressField suppressField = new SuppressField();
		assertEquals(' ', suppressField.getMyChar());
	}

	@Test
	public void assertThatSpecificInstanceFieldSuppressionWorks() throws Exception {
		suppress(field(SuppressField.class, "mySecondValue"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
	}

	@Test
	public void assertThatSpecificInstanceFieldSuppressionWhenSpecifyingClassAndFieldNameWorks() throws Exception {
		suppress(field(SuppressField.class, "mySecondValue"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
	}

	@Test
	public void assertThatMultipleInstanceFieldSuppressionWorks() throws Exception {
		suppress(fields(SuppressField.class, "mySecondValue", "myChar"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
		assertEquals(' ', suppressField.getMyChar());
		assertEquals(Boolean.TRUE, suppressField.getMyWrappedBoolean());
	}

	// TODO Add final tests here as well when they work
	@Test
	public void assertThatAllFieldSuppressionWorks() throws Exception {
		suppress(fields(SuppressField.class));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getMySecondValue());
		assertEquals(' ', suppressField.getMyChar());
		assertNull(suppressField.getMyWrappedBoolean());
		assertNull(SuppressField.getMyObject());
	}

	@Test
	public void assertThatObjectIsNeverInstantiated() throws Exception {
		suppress(field(SuppressField.class, "domainObject"));
		SuppressField suppressField = new SuppressField();
		assertNull(suppressField.getDomainObject());
	}
}
