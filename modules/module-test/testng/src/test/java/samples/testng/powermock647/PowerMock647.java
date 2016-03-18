package samples.testng.powermock647;

import org.testng.IResultMap;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;
import samples.testng.SimpleBaseTest;

import java.net.URL;
import java.net.URLClassLoader;

import static org.testng.Assert.assertEquals;

public class PowerMock647 extends SimpleBaseTest {

  private final TestListenerAdapter tla;

  public PowerMock647() {
    tla = new TestListenerAdapter();
  }

  @Test
  public void testSkipTest() throws Exception {

    final TestNG tng = createTestNG();

    runTest(tng);

    assertOneTestSkipped();
  }

  private TestNG createTestNG() {
    final TestNG tng = create(SkipExceptionTest.class);
    tng.setThreadCount(1);
    tng.setParallel(XmlSuite.ParallelMode.NONE);
    tng.setPreserveOrder(true);
    tng.addListener(tla);
    return tng;
  }

  private void assertOneTestSkipped() {
    IResultMap skippedTests = tla.getTestContexts().get(0).getSkippedTests();
    assertEquals(1, skippedTests.size());
  }

  private void runTest(TestNG tng) {

    ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
    ClassLoader classLoader = new SimpleClassLoader(currentClassLoader);

    Thread.currentThread().setContextClassLoader(classLoader);

    tng.run();

    Thread.currentThread().setContextClassLoader(currentClassLoader);
  }

  public static final class SimpleClassLoader extends ClassLoader {

    private final ClassLoader currentClassLoader;
    private final URLClassLoader delegate;

    public SimpleClassLoader(ClassLoader currentClassLoader) {
      this.currentClassLoader = currentClassLoader;
      this.delegate = new URLClassLoader(new URL[]{currentClassLoader.getResource("")}, null);
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      final Class<?> clazz;

      if (shouldBeLoadedWithDelegate(name)) {
        clazz = delegate.loadClass(name);
      } else {
        clazz = currentClassLoader.loadClass(name);
      }

      if (resolve) {
        resolveClass(clazz);
      }

      return clazz;
    }

    private boolean shouldBeLoadedWithDelegate(String name) {
      return "org.testng.SkipException".equals(name) || "test.testng1003.SkipExceptionTest".equals(name) ||
                 "test.testng1003.SomeClass".equals(name);
    }

  }
}
