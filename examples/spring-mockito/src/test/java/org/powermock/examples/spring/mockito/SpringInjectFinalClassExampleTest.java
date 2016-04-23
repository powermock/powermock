package org.powermock.examples.spring.mockito;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import powermock.examples.spring.FinalClass;
import powermock.examples.spring.MyBean;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalClass.class)
public class SpringInjectFinalClassExampleTest {

    @Mock
    private FinalClass finalClass;

    @InjectMocks
    private MyBean myBean = new MyBean();;

    @Test
    public void testInjectFinalClass() {
        final String value = "What's up?";
        when(finalClass.sayHello()).thenReturn(value);

        assertEquals(value, myBean.sayHello());

    }

}
