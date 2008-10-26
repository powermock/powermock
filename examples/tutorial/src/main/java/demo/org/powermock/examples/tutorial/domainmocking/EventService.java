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

import demo.org.powermock.examples.tutorial.domainmocking.domain.BusinessMessages;
import demo.org.powermock.examples.tutorial.domainmocking.domain.Person;

/**
 * A simple interface that manages events.
 */
public interface EventService {

	/**
	 * Sends a new error event to the interested parties in the system.
	 * 
	 * @param person
	 *            The person to object associated with this event.
	 * @param messages
	 *            The business messages object that may contain errors or
	 *            warnings.
	 */
	void sendErrorEvent(Person person, BusinessMessages messages);

}
