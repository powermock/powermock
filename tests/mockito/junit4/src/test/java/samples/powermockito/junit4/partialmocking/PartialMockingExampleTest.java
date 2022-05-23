/*
 * Copyright 2009 the original author or authors.
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
package samples.powermockito.junit4.partialmocking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.partialmocking.PartialMockingExample;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Asserts that partial mocking (spying) with PowerMockito works for non-final
 * methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PartialMockingExample.class)
public class PartialMockingExampleTest {
    
    @Test
    public void validatingSpiedObjectGivesCorrectNumberOfExpectedInvocations() throws Exception {
        final String expected = "TEST VALUE";
        PartialMockingExample underTest = spy(new PartialMockingExample());
        doReturn(expected).when(underTest).methodToMock();
        
        assertEquals(expected, underTest.methodToTest());
        
        verify(underTest).methodToTest();
        verify(underTest).methodToMock();
    }
    
    @Test
    public void validatingSpiedObjectGivesCorrectNumberOfExpectedInvocationsForMockito() throws Exception {
        final String expected = "TEST VALUE";
        PartialMockingExample underTest = Mockito.spy(new PartialMockingExample());
        doReturn(expected).when(underTest).methodToMock();
        
        assertEquals(expected, underTest.methodToTest());
        
        verify(underTest).methodToTest();
        verify(underTest).methodToMock();
    }
    
    @Test
    public void should_verify_spied_object_used_in_other_threads() {
        
        final String expected = "TEST VALUE";
        final PartialMockingExample underTest = spy(new PartialMockingExample());
        doReturn(expected).when(underTest).methodToMock();
        
        final int threadCounts = 10;
        
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCounts);
        final List<String> values = new CopyOnWriteArrayList<String>();
        
        for (int i = 0; i < threadCounts; i++) {
            createAndStartThread(i, underTest, startLatch, endLatch, values);
        }
        
        startLatch.countDown();
        
        awaitThreads(endLatch);
        
        assertThat(values)
            .as("All threads have called method and get expected result")
            .hasSize(threadCounts)
            .containsOnly(expected);
        
        verify(underTest, times(threadCounts)).methodToTest();
        verify(underTest, times(threadCounts)).methodToMock();
    }
    
    private void awaitThreads(final CountDownLatch endLatch) {
        try {
            endLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void createAndStartThread(final int index, final PartialMockingExample underTest,
                                      final CountDownLatch startLatch,
                                      final CountDownLatch endLatch,
                                      final List<String> values) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                values.add(underTest.methodToTest());
                endLatch.countDown();
            }
        };
        
        Thread thread = new Thread(runnable, "mock-use-" + index);
        
        thread.start();
    }
}
