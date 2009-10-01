package samples.junit4.suppressfield;

import static org.powermock.api.support.membermodification.MemberMatcher.field;
import static org.powermock.api.support.membermodification.MemberMatcher.fields;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import samples.suppressfield.ItemRepository;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { ItemRepository.class })
@SuppressStaticInitializationFor("samples.suppressfield.ItemRepository")
public class ItemRepositoryTest {
	@Test(expected = NullPointerException.class)
	public void testaddItem() throws Exception {
		suppress(fields(field(ItemRepository.class, "itemMap"), field(ItemRepository.class, "totalItems")));

		ItemRepository objRep = Whitebox.newInstance(ItemRepository.class);
		objRep.addItem("key", "value");
	}
}