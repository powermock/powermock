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
 * Simple class that uses a collaborator (the StateHolder class) which is final
 * and have final methods. The purpose is to show that PowerMock has the ability
 * to create a mock of the collaborator even though it's final and to expect the
 * method calls even though they're also final.
 */
public class StateFormatter {

	private final StateHolder stateHolder;

	public StateFormatter(StateHolder stateHolder) {
		this.stateHolder = stateHolder;
	}

	public String getFormattedState() {
		String safeState = "State information is missing";
		final String actualState = stateHolder.getState();
		if (actualState != null) {
			safeState = actualState;
		}
		return safeState;
	}
}
