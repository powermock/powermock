package samples.powermockito.junit4.stacktracecleaner;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.InvocationContainerImpl;
import org.mockito.internal.util.MockUtil;
import org.mockito.invocation.Location;
import org.mockito.invocation.MockHandler;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Stubbing;
import org.powermock.api.mockito.invocation.MockHandlerAdaptor;
import org.powermock.api.mockito.invocation.MockitoMethodInvocationControl;
import org.powermock.core.MockRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Ensures Location.toString returns the location where a mock is declared.  The filtering of the stack trace used
 * to determine the location is performed by the StackTraceCleaner in StackTraceCleanerProvider.
 */
@RunWith(value = PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.StrictStubs.class)
@PrepareForTest({SomethingWithStaticMethod.class})
public class LocationFromStackTraceTest {
    @Test
    public void should_filter_extra_elements_in_stack_when_mocking_static_method() throws Exception {
        mockStatic(SomethingWithStaticMethod.class);
        when(SomethingWithStaticMethod.doStaticOne()).thenReturn("Something else 1");
        when(SomethingWithStaticMethod.doStaticTwo()).thenReturn("Something else 2");
        doNothing().when(SomethingWithStaticMethod.class, "doStaticVoid");

        MockitoMethodInvocationControl invocationControl =
                (MockitoMethodInvocationControl)
                        MockRepository.getStaticMethodInvocationControl(SomethingWithStaticMethod.class);
        MockHandlerAdaptor mockHandlerAdaptor = invocationControl.getMockHandlerAdaptor();
        Object mock = mockHandlerAdaptor.getMock();
        MockHandler<Object> mockHandler = MockUtil.getMockHandler(mock);
        InvocationContainerImpl invocationContainer = (InvocationContainerImpl)mockHandler.getInvocationContainer();
        List<Stubbing> stubbings = new ArrayList<Stubbing>(invocationContainer.getStubbingsAscending());
        assertThat(stubbings.size(), is(3));
        Location static1Location = stubbings.get(0).getInvocation().getLocation();
        assertThat(static1Location.toString(), is("-> at samples.powermockito.junit4.stacktracecleaner." +
                "LocationFromStackTraceTest.should_filter_extra_elements_in_stack_when_mocking_static_method(" +
                "LocationFromStackTraceTest.java:37)"));
        Location static2Location = stubbings.get(1).getInvocation().getLocation();
        assertThat(static2Location.toString(), is("-> at samples.powermockito.junit4.stacktracecleaner." +
                "LocationFromStackTraceTest.should_filter_extra_elements_in_stack_when_mocking_static_method(" +
                "LocationFromStackTraceTest.java:38)"));
        Location staticVoidLocation = stubbings.get(2).getInvocation().getLocation();
        assertThat(staticVoidLocation.toString(), is("-> at samples.powermockito.junit4.stacktracecleaner." +
                "LocationFromStackTraceTest.should_filter_extra_elements_in_stack_when_mocking_static_method(" +
                "LocationFromStackTraceTest.java:39)"));
        
        // Removing these calls and the three Location assertions above will cause the test to fail due to the
        // strict stubs.  Without the alterations to StackTraceCleanerProvider, the failure messages will contain
        // an item in the stack trace inside the powermock libraries.
        assertThat(SomethingWithStaticMethod.doStaticOne(), is("Something else 1"));
        assertThat(SomethingWithStaticMethod.doStaticTwo(), is("Something else 2"));
        SomethingWithStaticMethod.doStaticVoid();
    }
}
