package samples.junit4.suppressfield;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.suppressfield.ItemRepository;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { ItemRepository.class })
@SuppressStaticInitializationFor("samples.suppressfield.ItemRepository")
public class ItemRepositoryTest {
    @Test(expected = NullPointerException.class)
    public void testaddItem() throws Exception {
        PowerMock.suppressField(ItemRepository.class, "itemMap");
        PowerMock.suppressField(ItemRepository.class, "totalItems");

        ItemRepository objRep = new ItemRepository();
        objRep.addItem("key", "value");
    }
}