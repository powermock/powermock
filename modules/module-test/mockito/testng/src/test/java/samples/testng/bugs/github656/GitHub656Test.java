package samples.testng.bugs.github656;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;

/**
 *
 */
@PrepareForTest({ GitHub656Test.SimpleFoo.class })
public class GitHub656Test extends PowerMockTestCase {

    @Test
    public void should_be_only_one_invocation() {
        GitHub656Test.SimpleFoo foo = PowerMockito.spy(new GitHub656Test.SimpleFoo());

        foo.setFoo(" ");

        verify(foo).setFoo(" ");
    }

    public static class SimpleFoo {

        private String foo;

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }
    }

}
