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
package samples.constructor;

import samples.Service;

/**
 * This class is used to demonstrate that error messages are correct when a
 * constructor is not found.
 */
public class PublicConstructorWithDependencyDemo {

	private final Service service;

	public PublicConstructorWithDependencyDemo(Service service) {
		this.service = service;
	}

	public Service getService() {
		return service;
	}

	public void aMethod() {
		System.out.println("Does basically nothing");
	}
}
