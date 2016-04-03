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
package samples.singleton;

import java.util.concurrent.Callable;

/**
 * Test class to demonstrate static, static+final, static+native and
 * static+final+native methods mocking.
 * 
 * @author Johan Haleby
 * @author Jan Kronquist
 */
public class StaticService {

	private static int number = 17;
    private static Integer intValue;
    public static String messageStorage;

    private int secret = 23;

    public static void sayHello(String message) {
        messageStorage = message;
    }

    public static void sayHello(Integer intValue) {
        StaticService.intValue = intValue;
    }

	public static int getNumberFromInner() {
		return new Callable<Integer>() {
			@Override
			public Integer call() {
				return number;
			}
		}.call();
	}

	public static int getNumberFromInnerInstance() {
		return new StaticService().internalGetNumberFromInnerInstance();
	}

	public int internalGetNumberFromInnerInstance() {
		return new Callable<Integer>() {
			@Override
			public Integer call() {
				return secret;
			}
		}.call();
	}

	public static String doStatic(int i) {
		throw new UnsupportedOperationException("method not implemented yet...");
	}

	public static void assertThatVerifyWorksForMultipleMocks() {
		StaticService.sayHello();
		StaticHelper.sayHelloHelper();
	}

	public static void sayHello() {
		StaticHelper.sayHelloHelper();
		StaticHelper.sayHelloHelper();
	}

	public static void sayHelloAgain() {
		StaticHelper.sayHelloAgain();
		StaticHelper.sayHelloAgain();
	}

	public static String say(String string) {
		return "Hello " + string;
	}

	public final static String sayFinal(String string) {
		return "Hello " + string;
	}

	public native static String sayNative(String string);

	public final native static String sayFinalNative(String string);

	public static int calculate(int a, int b) {
		return a + b;
	}

	private static String sayPrivateStatic(String string) {
		return "Hello private static " + string;
	}

	private static String sayPrivateFinalStatic(String string) {
		return "Hello private static " + string;
	}

	private static final native String sayPrivateNativeFinalStatic(String string);
}
