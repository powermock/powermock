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
package demo.org.powermock.examples.tutorial.partialmocking.service;

import java.util.Set;

import demo.org.powermock.examples.tutorial.partialmocking.domain.ServiceProducer;

/**
 * A simple interface that manages persons.
 */
public interface ProviderService {

	/**
	 * Get all service provider.
	 * 
	 * @return All service provider artifacts currently available or an empty
	 *         set if no service providers are available.
	 */
	Set<ServiceProducer> getAllServiceProviders();

	/**
	 * Get a service provider.
	 * 
	 * @param id
	 *            The id of the service provider to get.
	 * @return The service artifact that represents the service provider or
	 *         <code>null</code> if no Service Producer was found with that
	 *         id.
	 */
	ServiceProducer getServiceProvider(int id);
}
