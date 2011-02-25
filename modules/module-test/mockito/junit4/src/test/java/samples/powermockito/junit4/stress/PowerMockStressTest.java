/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samples.powermockito.junit4.stress;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.stress.ClassWithStatic;
import samples.stress.StressSample;

import static org.mockito.Mockito.*;

/**
 * Test that asserts that <a href="http://code.google.com/p/powermock/issues/detail?id=308">issue 308</a> is resolved.
 * The problem was that finalize methods could be called before an expected method because the GC kicked in too soon
 * since now object held a reference to the mock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClassWithStatic.class})
public class PowerMockStressTest {
	
	private StressSample underTest = new StressSample();

	@Before
	public void setUp(){
		PowerMockito.mockStatic(ClassWithStatic.class);
		for (int i = 0; i < 1000; i++) { // 100*8executions
			createWhen();
		}
	}
	public void createWhen(){
		when(ClassWithStatic.a()).thenReturn("A");
		when(ClassWithStatic.b()).thenReturn("B");
		when(ClassWithStatic.c()).thenReturn("C");
		when(ClassWithStatic.d()).thenReturn("D");
		when(ClassWithStatic.e()).thenReturn("E");
		when(ClassWithStatic.f()).thenReturn("F");
		when(ClassWithStatic.g()).thenReturn("G");
		when(ClassWithStatic.h()).thenReturn("H");
	}
	
	@Test
	public void test1(){
		underTest.a1();
	}
	@Test
	public void test2(){
		underTest.b1();
	}
	@Test
	public void test3(){
		underTest.c1();
	}
	@Test
	public void test4(){
		underTest.d1();
	}
	@Test
	public void test5(){
		underTest.e1();
	}
	@Test
	public void test6(){
		underTest.f1();
	}
	@Test
	public void test7(){
		underTest.g1();
	}
	@Test
	public void test8(){
		underTest.h1();
	}
}
