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
package samples.junit4;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import samples.junit4.constructorargs.ConstructorArgsDemoTest;
import samples.junit4.expectvoid.ExpectVoidDemoTest;
import samples.junit4.finalmocking.FinalDemoTest;
import samples.junit4.newmocking.StupidNewTest;
import samples.junit4.noannotation.NoAnnotationUsageTest;
import samples.junit4.partialmocking.MockSelfDemoTest;
import samples.junit4.partialmocking.MockSelfDemoWithSubClassTest;
import samples.junit4.prepareeverything.ExpectNewDemoUsingThePrepareEverythingAnnotationTest;
import samples.junit4.privateandfinal.PrivateFinalTest;
import samples.junit4.privatefield.MockSelfPrivateFieldServiceClassTest;
import samples.junit4.privatefield.SimplePrivateFieldServiceClassTest;
import samples.junit4.privatemocking.PrivateMethodDemoTest;
import samples.junit4.simplereturn.SimpleReturnExampleUserTest;
import samples.junit4.singleton.MockStaticTest;
import samples.junit4.staticandinstance.StaticAndInstanceDemoTest;
import samples.junit4.staticinitializer.StaticInitializerExampleTest;
import samples.junit4.suppressconstructor.SuppressConstructorDemoTest;
import samples.junit4.suppressconstructor.SuppressConstructorHierarchyDemoTest;
import samples.junit4.suppressmethod.SuppressMethodTest;
import samples.suppressconstructor.SuppressSpecificConstructorDemoTest;

// This test verifies that the SuppressConstructorHierachyDemo works, issue http://code.google.com/p/powermock/issues/detail?id=43.

@RunWith(Suite.class)
@SuiteClasses( { ExpectVoidDemoTest.class, FinalDemoTest.class, MockSelfDemoTest.class, MockSelfDemoWithSubClassTest.class, StupidNewTest.class,
		PrivateFinalTest.class, MockSelfPrivateFieldServiceClassTest.class, SimplePrivateFieldServiceClassTest.class, PrivateMethodDemoTest.class,
		MockStaticTest.class, StaticAndInstanceDemoTest.class, SuppressMethodTest.class, SuppressConstructorDemoTest.class,
		SuppressConstructorHierarchyDemoTest.class, SuppressSpecificConstructorDemoTest.class, ConstructorArgsDemoTest.class,
		NoAnnotationUsageTest.class, SimpleReturnExampleUserTest.class, StaticInitializerExampleTest.class,
		ExpectNewDemoUsingThePrepareEverythingAnnotationTest.class })
public class SomeJUnit4Tests {
}
