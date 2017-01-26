package samples.powermockito.junit4.bugs.github717;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.extension.listener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
@PrepareForTest(InstanceFacadeImpl.class)
public class InstanceFacadeImplTest {
    
    private InstanceFacadeImpl instanceFacade;
    
    @Before
    public void setup() throws Exception {
        instanceFacade = new InstanceFacadeImpl();
    }
    
    @Test
    public void should_not_throw_exception() throws Exception {
        replayAll();
        instanceFacade.instanceStatusProcessors.get(InstanceStatus.PENDING).accept(null);
        verifyAll();
    }
    
}
