package samples.powermockito.junit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;

/**
 * @author Anderson Borba
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TestClass.class)
public class ClassLoaderBugTest {

    /**
     * See issue <a href="https://code.google.com/p/powermock/issues/detail?id=426">426</a> for more details.
     */
    @Test(timeout = 2000)
    public void resourcesAreNotLoadedTwice() throws IOException {
    	String resourceName = getClass().getCanonicalName().replace(".", "/") + ".class";
        Enumeration<URL> enumeration = getClass().getClassLoader().getResources(resourceName);

        int count = 0;
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement().toString());
            count++;
        }

        assertEquals(1, count);
    }

}

class TestClass {

}
