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
package demo.org.powermock.examples.tutorial.domainmocking;

/**
 * A simple service interface.
 */
public interface SampleService {

	/**
	 * Create a new person based on the following parameters and store it in the
	 * underlying persistence store. The service will notify the result of the
	 * operation to an event service.
	 * 
	 * @param firstName
	 *            The first name of the person to create.
	 * @param lastName
	 *            The last name of the person to create.
	 * @return <code>true</code> if the person was created successfully,
	 *         <code>false</code> otherwise.
	 */
	boolean createPerson(String firstName, String lastName);

}
