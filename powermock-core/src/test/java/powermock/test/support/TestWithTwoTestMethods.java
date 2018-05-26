package powermock.test.support;

import org.junit.Test;

public class TestWithTwoTestMethods {
    
    @Test
    public void test_method_1() {
    }
    
    @Test
    public void test_method_2() {
    
    }
    
    
    public static class NestedTestWithTwoTestMethods {
        
        @Test
        public void test_nested_method_1() {
        }
        
        @Test
        public void test_nested_method_2() {
        
        }
        
    }
}
