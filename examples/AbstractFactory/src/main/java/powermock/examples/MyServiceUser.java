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
package powermock.examples;

import java.util.Set;

import powermock.examples.dependencymanagement.DependencyManager;
import powermock.examples.domain.Person;
import powermock.examples.service.MyService;

/**
 * A simple service class that uses the {@link DependencyManager} to get the
 * {@link MyService} singleton instance. This is the class that we want to test.
 * What's interesting in this example is the static call to the
 * <code>DependencyManager</code>. Without byte-code manipulation (provided
 * in this example by PowerMock) it would not be possible to return a mock from
 * the call to
 * 
 * <pre>
 * DependencyManager.getInstance();
 * </pre>
 * 
 * The purpose of this example is to demonastrate how to mock that static
 * method.
 */
public class MyServiceUser {

	public int getNumberOfPersons() {
		MyService myService = DependencyManager.getInstance().getMyService();
		Set<Person> allPersons = myService.getAllPersons();
		return allPersons.size();
	}

}
