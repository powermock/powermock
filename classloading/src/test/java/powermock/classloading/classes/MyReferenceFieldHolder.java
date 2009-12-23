/*
 * Copyright 2009 the original author or authors.
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
package powermock.classloading.classes;

public class MyReferenceFieldHolder {
	public static final MyArgument MY_ARGUMENT = new MyArgument("testing");

	private MyArgument myArgument1 = MY_ARGUMENT;
	private MyArgument myArgument2 = MY_ARGUMENT;

	public MyArgument getMyArgument1() {
		return myArgument1;
	}

	public MyArgument getMyArgument2() {
		return myArgument2;
	}
}
