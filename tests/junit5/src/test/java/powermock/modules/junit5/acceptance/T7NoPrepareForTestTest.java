package powermock.modules.junit5.acceptance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.modules.junit5.PowerMockExtension;
import samples.Service;
import samples.newmocking.MyClass;
import samples.singleton.StaticService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** T7: the extension without @PrepareForTest must not break plain code and plain Mockito mocks. */
@ExtendWith(PowerMockExtension.class)
class T7NoPrepareForTestTest {

    @Test
    void realCodeRuns() {
        assertEquals("Hello world", StaticService.say("world"));
        assertEquals("Hello world", new MyClass().getMessage());
    }

    @Test
    void plainMockitoMocksWork() {
        Service service = mock(Service.class);
        when(service.getServiceMessage()).thenReturn("mocked");
        MyClass myClass = mock(MyClass.class);
        when(myClass.getMessage()).thenReturn("mocked class");

        assertEquals("mocked", service.getServiceMessage());
        assertEquals("mocked class", myClass.getMessage());
        verify(service).getServiceMessage();
    }
}
