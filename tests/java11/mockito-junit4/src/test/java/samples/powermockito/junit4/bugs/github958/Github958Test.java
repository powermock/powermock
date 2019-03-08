package samples.powermockito.junit4.bugs.github958;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OuterClass.class)
public class Github958Test {
  @Test
  public void test() {
    assertEquals("inner", OuterClass.theInstance.name);
  }
}
