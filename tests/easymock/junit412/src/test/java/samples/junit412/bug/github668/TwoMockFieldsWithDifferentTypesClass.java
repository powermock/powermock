package samples.junit412.bug.github668;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.extension.listener.AnnotationEnabler;
import org.powermock.core.classloader.annotations.PowerMockListener;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.Service;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.easymock.PowerMock.replayAll;


@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
public class TwoMockFieldsWithDifferentTypesClass {

    @Mock
    private IncidentPropertyChangeDAO incidentPropertyChangeDAO;

    @Mock
    private Service serviceMock;

    @Test
    public void mockClassShouldInjected() {
        assertNotNull(incidentPropertyChangeDAO);
        assertNotNull(serviceMock);
    }
    
    @Test
    public void shouldBeAbleMockMethodsOfInjected() {
        expect(incidentPropertyChangeDAO.getIncident()).andReturn("value");
        expect(serviceMock.getServiceMessage()).andReturn("value");

        replayAll(incidentPropertyChangeDAO, serviceMock);

        assertEquals("value", incidentPropertyChangeDAO.getIncident());
        assertEquals("value", serviceMock.getServiceMessage());
    }
}
