package org.powermock.modules.junit4.internal.impl.testcaseworkaround;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.MethodValidator;
import org.junit.internal.runners.TestClass;
import org.powermock.reflect.Whitebox;

/**
 * A custom {@link MethodValidator} that makes sure that test methods not
 * annotated by the Test annotation works in JUnit 4.4 with the custom
 * JUnit-runner when the test class is extending {@link TestCase}. This is
 * actually a workaround for JUnit 4.4 when the test case extends from the
 * <code>TestCase</code> class.
 */
public class PowerMockJUnit4MethodValidator extends MethodValidator {

	public PowerMockJUnit4MethodValidator(TestClass testClass) {
		super(testClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validateInstanceMethods() {
		validateTestMethods(After.class, false);
		validateTestMethods(Before.class, false);
		validateTestMethods(Test.class, false);

		TestClass testClass = (TestClass) Whitebox.getInternalState(this, "fTestClass", MethodValidator.class);
		Class<?> classUnderTest = (Class<?>) Whitebox.getInternalState(testClass, "fClass");
		List<Throwable> fErrors = (List<Throwable>) Whitebox.getInternalState(this, "fErrors", MethodValidator.class);

		List<Method> methods = getTestMethods(testClass, classUnderTest);
		if (methods.size() == 0)
			fErrors.add(new Exception("No runnable methods"));
	}

	private List<Method> getTestMethods(TestClass testClass, Class<?> classUnderTest) {
		List<Method> methods = testClass.getAnnotatedMethods(Test.class);
		if (methods.isEmpty()) {
			methods.addAll(getTestMethodsWithNoAnnotation(classUnderTest));
		}
		return methods;
	}

	/**
	 * This is a rip-off of the
	 * {@link MethodValidator#validateInstanceMethods()} with the exception that
	 * this method also searches for test methods if the class extends
	 * {@link TestCase} and has methods that starts with test which are not
	 * annotated.
	 */
	@SuppressWarnings("unchecked")
	private void validateTestMethods(Class<? extends Annotation> annotation, boolean isStatic) {
		TestClass testClass = (TestClass) Whitebox.getInternalState(this, "fTestClass", MethodValidator.class);
		Class<?> classUnderTest = (Class<?>) Whitebox.getInternalState(testClass, "fClass");
		List<Method> methods = null;
		if ((methods == null || methods.isEmpty()) && TestCase.class.equals(classUnderTest.getSuperclass()) && !isStatic) {
			methods = getTestMethodsWithNoAnnotation(classUnderTest);
		} else {
			methods = testClass.getAnnotatedMethods(annotation);
		}

		List<Throwable> fErrors = (List<Throwable>) Whitebox.getInternalState(this, "fErrors", MethodValidator.class);
		for (Method each : methods) {
			if (Modifier.isStatic(each.getModifiers()) != isStatic) {
				String state = isStatic ? "should" : "should not";
				fErrors.add(new Exception("Method " + each.getName() + "() " + state + " be static"));
			}
			if (!Modifier.isPublic(each.getDeclaringClass().getModifiers()))
				fErrors.add(new Exception("Class " + each.getDeclaringClass().getName() + " should be public"));
			if (!Modifier.isPublic(each.getModifiers()))
				fErrors.add(new Exception("Method " + each.getName() + " should be public"));
			if (each.getReturnType() != Void.TYPE)
				fErrors.add(new Exception("Method " + each.getName() + " should be void"));
			if (each.getParameterTypes().length != 0)
				fErrors.add(new Exception("Method " + each.getName() + " should have no parameters"));
		}
	}

	private List<Method> getTestMethodsWithNoAnnotation(Class<?> testClass) {
		List<Method> potentialTestMethods = new LinkedList<Method>();
		Method[] methods = testClass.getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("test")) {
				potentialTestMethods.add(method);
			}
		}
		return potentialTestMethods;
	}
}
