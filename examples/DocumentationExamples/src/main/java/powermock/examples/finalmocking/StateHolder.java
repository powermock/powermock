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
package powermock.examples.finalmocking;

/**
 * A dummy class created to demonstrated how PowerMock can deal with final
 * classes and methods.
 */
public final class StateHolder {

	/**
	 * Dummy method that is used to demonstrate how PowerMock can deal with
	 * final methods.
	 * 
	 * @return The current state.
	 */
	public final String getState() {
		// Imagine that we query a database for state
		return null;
	}

	/**
	 * Dummy method that is used to demonstrate how PowerMock can deal with
	 * final methods.
	 */
	public final void setState(String state) {
		// Imagine that we store the state in a database.
	}
}
