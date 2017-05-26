package samples.powermockito.junit4.privatemocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import samples.privatemocking.PrivateMethodDemo;

import javax.activation.FileDataSource;
import java.io.File;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.isA;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PrivateMethodDemo.class})
public class PrivateInstanceMockingTest extends PrivateInstanceMockingCases {
    
    @Test
    public void expectationsWorkWhenSpyingOnPrivateMethodsUsingDoReturn() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());
        assertEquals("Hello Temp, you are 50 old.", tested.sayYear("Temp", 50));

        doReturn("another").when(tested, "doSayYear", 12, "test");

        assertEquals("Hello Johan, you are 29 old.", tested.sayYear("Johan", 29));
        assertEquals("another", tested.sayYear("test", 12));

        verifyPrivate(tested).invoke("doSayYear", 12, "test");
    }

    @Test
    public void expectationsWorkWhenSpyingOnPrivateMethodsUsingDoReturnWhenMethodDoesntHaveAnyArguments() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());

        doReturn("another").when(tested, "sayIt");

        assertEquals("another", Whitebox.invokeMethod(tested, "sayIt"));

        verifyPrivate(tested).invoke("sayIt");
    }

    @Test
    public void verifyPrivateMethodWhenNoExpectationForTheMethodHasBeenMade() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());

        assertEquals("Hello Johan, you are 29 old.", tested.sayYear("Johan", 29));

        verifyPrivate(tested).invoke("doSayYear", 29, "Johan");
    }
    
    @Test(expected = ArrayStoreException.class)
    public void expectationsWorkWhenSpyingOnPrivateVoidMethods() throws Exception {
        PrivateMethodDemo tested = spy(new PrivateMethodDemo());

        tested.doObjectStuff(new Object());

        when(tested, "doObjectInternal", isA(Object.class)).thenThrow(new ArrayStoreException());

        tested.doObjectStuff(new Object());
    }
    
    @Test
    public void usingMultipleArgumentsOnPrivateMethodWorks() throws Exception {
        File file = mock(File.class);
        FileDataSource fileDataSource = mock(FileDataSource.class);
        StringReader expected = new StringReader("Some string");

        PrivateMethodDemo tested = mock(PrivateMethodDemo.class);
        doReturn(expected).when(tested, method(PrivateMethodDemo.class, "createReader", File.class, FileDataSource.class)).withArguments(file, fileDataSource);

        StringReader actual = Whitebox.invokeMethod(tested, "createReader", file, fileDataSource);

        assertSame(expected, actual);
    }
}
