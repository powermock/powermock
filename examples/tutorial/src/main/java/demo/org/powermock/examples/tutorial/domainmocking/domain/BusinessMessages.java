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
package demo.org.powermock.examples.tutorial.domainmocking.domain;

import java.util.Random;

/**
 * A simple domain object that's pretended to hold state of one or more
 * operation outcomes. We pretend that services can store messages or errors
 * (for example validation errors) in this message (even though these methods
 * are not implemented or even defined here).
 */
public class BusinessMessages {

	/**
	 * @return <code>true</code> if the an error has occurred when invoking an
	 *         operation, <code>false</code> otherwise.
	 */
	public boolean hasErrors() {
		return new Random().nextBoolean();
	}

}
