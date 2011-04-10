package samples.testng.agent;

import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import samples.Service;
import samples.annotationbased.AnnotationDemo;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Verifies that PowerMock test listeners works correctly in TestNG.
 */
@PrepareForTest
public class AnnotationDemoTest extends PowerMockTestCase {

    @Mock
    private Service serviceMock;

    @Test
    public void assertInjectionWorked() throws Exception {
        AnnotationDemo tested = new AnnotationDemo(serviceMock);
        final String expected = "mock";
        expect(serviceMock.getServiceMessage()).andReturn(expected);

        replayAll();

        Assert.assertEquals(expected, tested.getServiceMessage());

        verifyAll();
    }
}
