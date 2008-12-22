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
package samples.suppressfield;

public class SuppressField {

	private static final int MY_VALUE = 8;

	private static final Object MY_OBJECT = new Object();

	private final boolean myBoolean = true;

	private final Boolean myWrappedBoolean = Boolean.TRUE;

	private final Object mySecondValue = new Object();

	private DomainObject domainObject = new DomainObject();

	public Object getMySecondValue() {
		return mySecondValue;
	}

	public DomainObject getDomainObject() {
		return domainObject;
	}

	private char myChar = 'a';

	public static int getMyValue() {
		return MY_VALUE;
	}

	public static Object getMyObject() {
		return MY_OBJECT;
	}

	public boolean isMyBoolean() {
		return myBoolean;
	}

	public Boolean getMyWrappedBoolean() {
		return myWrappedBoolean;
	}

	public char getMyChar() {
		return myChar;
	}
}
