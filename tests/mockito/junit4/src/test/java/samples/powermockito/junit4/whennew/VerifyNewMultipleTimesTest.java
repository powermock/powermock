/*
 * Copyright 2010 the original author or authors.
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
package samples.powermockito.junit4.whennew;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.expectnew.NewFileExample;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NewFileExample.class)
public class VerifyNewMultipleTimesTest {
    private final static String DIRECTORY_PATH = "mocked path";
    @Mock
    private File directoryMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        whenNew(File.class).withArguments(DIRECTORY_PATH).thenReturn(directoryMock);

        when(directoryMock.exists()).thenReturn(false);
        when(directoryMock.mkdirs()).thenReturn(true);
    }

    @Test(expected=AssertionError.class)
    public void verifyNewTooManyTimesCausesAssertionError() throws Exception {
        assertTrue(new NewFileExample().createDirectoryStructure((DIRECTORY_PATH)));

        verify(directoryMock).mkdirs();

        // Correct usage
        verifyNew(File.class, times(1)).withArguments(DIRECTORY_PATH);

        // Should throw
        verifyNew(File.class, times(100000)).withArguments(DIRECTORY_PATH);
    }
}
