package samples.powermockito.junit4.bugs.github583;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {ChildClass.class})
public class ClassWithInheritanceTest {
    @Spy
    ChildClass b = new ChildClass();

    @Test
    public void test_test(){
        b.test();
    }
}
