package samples.powermockito.junit4.bugs.github731;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.ExecutionException;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class MockingInterfacesTest {
    
    @Test
    public void should_stub_future_get_method() throws ExecutionException, InterruptedException {
        
        SomeInterface<OptionalInterface<AType>> mockFuture = mock(SomeInterface.class);
        
        OptionalInterface<AType> mockTypeOpt = mock(OptionalInterface.class);
        
        when(mockFuture.get()).thenReturn(mockTypeOpt);
    }
}
