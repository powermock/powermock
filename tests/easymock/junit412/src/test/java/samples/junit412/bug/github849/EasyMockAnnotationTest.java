package samples.junit412.bug.github849;

import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.Service;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
public class EasyMockAnnotationTest {
    
    @Mock
    private Service serviceMock;
    
    @Test
    public void replayAll_should_switch_to_replay_mode_mocks_created_by_easymock_annotation_processing() {
        expect(serviceMock.getServiceMessage()).andReturn("value");
    
        PowerMock.replayAll();
        
        assertEquals("value", serviceMock.getServiceMessage());
    }
}
