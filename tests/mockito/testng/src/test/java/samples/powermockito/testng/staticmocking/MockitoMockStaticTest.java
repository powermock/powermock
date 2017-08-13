/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samples.powermockito.testng.staticmocking;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import samples.singleton.StaticHelper;
import samples.singleton.StaticService;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Test class to demonstrate static, static+final, static+native and
 * static+final+native methods mocking.
 */
@PrepareForTest({StaticService.class, StaticHelper.class})
public class MockitoMockStaticTest {
    
    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
    
    
    @Test
    public void testMockStatic() throws Exception {
        
        System.out.println("Skip test while Mockito doesn't deliver fix");
        
        mockStatic(StaticService.class);
        String expected = "Hello altered World";
        when(StaticService.say("hello")).thenReturn("Hello altered World");
        
        String actual = StaticService.say("hello");
        
        verifyStatic(StaticService.class);
        StaticService.say("hello");
        
        Assert.assertEquals(expected, actual);
    }
    
    
    @Test
    public void testMockStaticFinal() throws Exception {
        
        System.out.println("Skip test while Mockito doesn't deliver fix");
        
        mockStatic(StaticService.class);
        String expected = "Hello altered World";
        when(StaticService.sayFinal("hello")).thenReturn("Hello altered World");
        
        String actual = StaticService.sayFinal("hello");
        
        verifyStatic(StaticService.class);
        StaticService.sayFinal("hello");
        
        Assert.assertEquals(expected, actual);
    }
}
