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
package samples.expectnew;

import samples.Service;
import samples.newmocking.MyClass;

import java.io.*;
import java.util.Date;

public class ExpectNewDemo {

	private int dummyField;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dummyField;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExpectNewDemo other = (ExpectNewDemo) obj;
		if (dummyField != other.dummyField)
			return false;
		return true;
	}

	public String getMessage() {
		MyClass myClass = new MyClass();
		return myClass.getMessage();
	}

	public String getMessageWithArgument() {
		MyClass myClass = new MyClass();
		return myClass.getMessage("test");
	}

	public void invokeVoidMethod() {
		MyClass myClass = new MyClass();
		myClass.voidMethod();
	}

	/**
	 * The purpose of the method is to demonstrate that a test case can mock the
	 * new instance call and throw an exception upon instantiation.
	 */
	public void throwExceptionWhenInvocation() {
		new MyClass();
	}

	/**
	 * The purpose of the method is to demonstrate that a test case can mock the
	 * new instance call and throw an exception upon instantiation.
	 */
	public void throwExceptionAndWrapInRunTimeWhenInvocation() {
		try {
			new MyClass();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String multipleNew() {
		MyClass myClass1 = new MyClass();
		MyClass myClass2 = new MyClass();

		final String message1 = myClass1.getMessage();
		final String message2 = myClass2.getMessage();
		return message1 + message2;
	}

	public void simpleMultipleNew() {
		new MyClass();
		new MyClass();
		new MyClass();
	}

	@SuppressWarnings("unused")
	private void simpleMultipleNewPrivate() {
		new MyClass();
		new MyClass();
		new MyClass();
	}

	public void simpleSingleNew() {
		new MyClass();
	}

	public Date makeDate() {
		return new Date();
	}
	
	public boolean fileExists(String name) {
		return new File(name).exists();
	}

	public InputStream alternativePath() {
		try {
			return new DataInputStream(null);
		} catch (RuntimeException e) {
			return new ByteArrayInputStream(new byte[0]);
		}
	}

	public String newWithArguments(Service service, int times) {
		return new ExpectNewServiceUser(service, times).useService();
	}

	public String newWithWrongArguments(Service service, int times) {
		return new ExpectNewServiceUser(service, times * 2).useService();
	}

	public String[] newVarArgs(String... strings) {
		return new VarArgsConstructorDemo(strings).getAllMessages();
	}

	public Service[] newVarArgs(Service... services) {
		return new VarArgsConstructorDemo(services).getAllServices();
	}
    public int[] newVarArgs(float myFloat, int ... ints) {
		return new VarArgsConstructorDemo(myFloat, ints).getInts();
	}

	public byte[][] newVarArgs(byte[]... bytes) {
		return new VarArgsConstructorDemo(bytes).getByteArrays();
	}

	public byte[][] newVarArgsWithMatchers() {
		return new VarArgsConstructorDemo(new byte[] { 42 }, new byte[] { 17 }).getByteArrays();
	}

	public void fileWriter(String name, String msg) throws IOException {
		new FileWriter(name).write(msg);
	}

	public void fileWriterPrint(String name, String msg) throws IOException {
		new PrintWriter(new FileWriter(name)).write(msg);
	}

	public byte[][] newSimpleVarArgs(byte[]... bytes) {
		return new SimpleVarArgsConstructorDemo(bytes).getByteArrays();
	}

    public Target createTarget(ITarget target) {
        Target domainTarget;
        try {
            domainTarget = new Target(getTargetName(target), target.getId());
        } catch (CreationException e) {
            domainTarget = new Target("Unknown", -1);
        }
        return domainTarget;
    }

    private String getTargetName(ITarget target) {
        return target.getName();
    }
}
