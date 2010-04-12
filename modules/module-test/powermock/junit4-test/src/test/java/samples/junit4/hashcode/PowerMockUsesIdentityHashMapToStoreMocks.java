package samples.junit4.hashcode;

import static org.powermock.api.easymock.PowerMock.createMock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.hashcode.HashCodeInitializedInCtor;

@RunWith(PowerMockRunner.class)
public class PowerMockUsesIdentityHashMapToStoreMocks {

     @Test
     public void storesObjectsToAutomaticallyReplayAndVerifyToAnIdentityHashMap()throws Exception {
       createMock(HashCodeInitializedInCtor.class);
     }
}
