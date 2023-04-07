package samples.powermockito.junit4.bugs.github1154;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.junit.runner.RunWith;
import junit.framework.TestCase;

class NestedClasses {
  private static Foo foo = new Foo();
  private static Bar bar = new Bar();
  static class Foo {
    void foo(Bar.Baz baz) {}
  }
  static class Bar {
    static class Baz {
    }
  }
}

@RunWith(PowerMockRunner.class)
@PrepareForTest({NestedClasses.class})
public class Github1154Test extends TestCase {
  public void test() throws Exception {
    PowerMockito.mockStatic(NestedClasses.class);
  }
}
