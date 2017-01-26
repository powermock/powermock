package samples.powermockito.junit4.bugs.github717;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InstanceFacadeImpl implements InstanceFacade {
    
    final Map<InstanceStatus, Consumer<Instance>> instanceStatusProcessors = new HashMap<>();
    
    {
        instanceStatusProcessors.put(InstanceStatus.PENDING, instance -> {
            // NOP
        });
    }
    
}
