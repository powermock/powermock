package samples.testng;

import org.testng.annotations.Test;

import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentBuilderFactoryTest {

    @Test
    public void classesNotAnnotatedWithPrepareForTestAreNotLoadedByByPowerMockCl() throws Exception {
        DocumentBuilderFactory.newInstance();
    }
}
