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
package samples.anonymousmocking;

import samples.Service;

public class StupidAnonymous {

	// TODO We need to make sure that you also can mock private members from an
	// inner class!
	protected Service service;

	public void setService(Service service) {
		this.service = service;
	}

	public String getMessageFromMyClass() {

		MyAbstractClass myclass = new MyAbstractClass() {
			@Override
			public String getMessage() {
				return "Hello world!";
			}
		};

		return myclass.getMessage();
	}

	public String getMessagesFromSeveralInnerClasses() {

		MyAbstractClass myclass1 = new MyAbstractClass() {
			@Override
			public String getMessage() {
				return "Hello world 1!";
			}
		};

		MyAbstractClass myclass2 = new MyAbstractClass() {
			@Override
			public String getMessage() {
				return "Hello world 2!";
			}
		};
		return myclass1.getMessage() + " " + myclass2.getMessage();
	}

	public String getServiceMessageFromInnerClass() {

		MyAbstractClass myclass = new MyAbstractClass() {
			@Override
			public String getMessage() {
				return service.getServiceMessage();
			}
		};

		return myclass.getMessage();
	}

	public String getMessageFromOtherMethodInInnerClass() {

		MyAbstractClass myclass = new MyAbstractClass() {
			@Override
			public String getMessage() {
				return returnThisMessage();
			}

			public String returnThisMessage() {
				return "A message";
			}
		};

		return myclass.getMessage();
	}
}
