package org.powermock.modules.testng;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.transformers.MockTransformer;
import org.powermock.core.transformers.impl.MainMockTransformer;
import org.powermock.tests.utils.impl.MockPolicyInitializerImpl;
import org.testng.IObjectFactory;

@SuppressWarnings("serial")
public class PowerMockObjectFactory implements IObjectFactory {

  ClassLoader mockLoader;
  
  public PowerMockObjectFactory() {
    List<MockTransformer> mockTransformerChain = new ArrayList<MockTransformer>();
    final MainMockTransformer mainMockTransformer = new MainMockTransformer();
    mockTransformerChain.add(mainMockTransformer);

    String[] classesToLoadByMockClassloader = new String[] { MockClassLoader.MODIFY_ALL_CLASSES };
    String[] packagesToIgnore = new String[0];
    mockLoader = new MockClassLoader(classesToLoadByMockClassloader, packagesToIgnore);
    ((MockClassLoader) mockLoader).setMockTransformerChain(mockTransformerChain);
    
  }
  
  @SuppressWarnings("unchecked")
  public Object newInstance(Constructor constructor, Object... params) {
    Class<?> testClass = constructor.getDeclaringClass();
    new MockPolicyInitializerImpl(testClass).initialize(mockLoader);
    try {
      final Class<?> testClassLoadedByMockedClassLoader = Class.forName(testClass.getName(), false, mockLoader);
      //TODO listeners of some kind?
      //TODO marshall ctor parameter types?
      Constructor<?> con = testClassLoadedByMockedClassLoader.getConstructor(constructor.getParameterTypes());
      return con.newInstance(params);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
}