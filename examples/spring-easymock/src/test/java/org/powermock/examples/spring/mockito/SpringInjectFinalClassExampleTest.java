package org.powermock.examples.spring.mockito;

import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import powermock.examples.spring.FinalClass;
import powermock.examples.spring.MyBean;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalClass.class)
public class SpringInjectFinalClassExampleTest {

    @Mock
    private FinalClass finalClass;

    @TestSubject
    private MyBean myBean = new MyBean();;

    @Test
    public void testInjectFinalClass() {
        final String value = "What's up?";
        expect(finalClass.sayHello()).andReturn(value);
        replay(finalClass);

        assertEquals(value, myBean.sayHello());

    }

}
