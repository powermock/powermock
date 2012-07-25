package org.powermock.core;

public class ClassLocator extends SecurityManager {
	public static Class getCallerClass() {
		return new ClassLocator().getClassContext()[4];
	}
}