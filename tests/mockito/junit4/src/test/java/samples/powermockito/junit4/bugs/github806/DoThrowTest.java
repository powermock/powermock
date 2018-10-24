/*
 *
 *   Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package samples.powermockito.junit4.bugs.github806;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.doThrow;

public class DoThrowTest {
    
    @Test(expected = RuntimeException.class)
    public void should_throw_expected_exception() {
        final DoThrowTMockClass mock = Mockito.mock(DoThrowTMockClass.class);
        doThrow(RuntimeException.class).when(mock).doSomething();
        mock.doSomething();
    }
    
    @Test(expected = CustomException.class)
    public void should_throw_custom_exception() throws CustomException {
        new DoThrowTMockClass().throwExceptionForInput("123");
    }
    
}
