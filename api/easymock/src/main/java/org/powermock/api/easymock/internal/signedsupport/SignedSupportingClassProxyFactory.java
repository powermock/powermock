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
package org.powermock.api.easymock.internal.signedsupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.easymock.classextension.ConstructorArgs;
import org.easymock.classextension.internal.BridgeMethodResolver;
import org.easymock.classextension.internal.ClassExtensionHelper;
import org.easymock.classextension.internal.ClassInstantiatorFactory;
import org.easymock.classextension.internal.ClassProxyFactory;
import org.easymock.internal.IProxyFactory;
import org.easymock.internal.ObjectMethodsFilter;

/**
 * Essentially a rip-off of the {@link ClassProxyFactory} in EasyMock class
 * extensions. There are two reasons for doing this. The first one is to change
 * the CGLib NamingPolicy to allow us to mock signed classes/jar files. The
 * second is to allow partial mocking of specific methods in a class hierarchy.
 * For example if A extends B and both have a method called "mockMe" (A
 * overrides B's mockMe method) and the user likes to mock <i>only</i> the
 * "mockMe" method of B, not A.
 * 
 * Unfortunately EasyMock doesn't supply us with enough hooks to do this in a
 * nice way.
 */
public class SignedSupportingClassProxyFactory<T> implements IProxyFactory<T> {

	@SuppressWarnings("unchecked")
	public T createProxy(Class<T> toMock, final InvocationHandler handler) {

		// Dirty trick to fix ObjectMethodsFilter
		// It will replace the equals, hashCode, toString methods it kept that
		// are the ones
		// from Object.class by the correct ones since they might have been
		// overloaded
		// in the mocked class.
		try {
			updateMethod(handler, toMock.getMethod("equals", new Class[] { Object.class }));
			updateMethod(handler, toMock.getMethod("hashCode", new Class[0]));
			updateMethod(handler, toMock.getMethod("toString", new Class[0]));
		} catch (NoSuchMethodException e) {
			// ///CLOVER:OFF
			throw new InternalError("We strangly failed to retrieve methods that always exist on an object...");
			// ///CLOVER:ON
		}

		MethodInterceptor interceptor = new ClassProxyFactory.MockMethodInterceptor(handler) {
            private static final long serialVersionUID = -2764609150597725248L;
            private Set<Method> mockedMethods;

			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

				// Bridges should be called so they can forward to the real
				// method
				if (method.isBridge()) {
					Method m = BridgeMethodResolver.findBridgedMethod(method);
					return handler.invoke(obj, m, args);
				}

				// We conveniently mock abstract methods by default
				if (Modifier.isAbstract(method.getModifiers())) {
					return handler.invoke(obj, method, args);
				}

				// Class<?> superclass =
				// method.getDeclaringClass().getSuperclass();
				// if (superclass != null && mockedMethods != null &&
				// !mockedMethods.contains(method)) {
				// /*
				// * Added the following if statement to allow partial mocking
				// * class hierarchy
				// */
				// Method superClassMethod = null;
				// try {
				// superClassMethod = WhiteboxImpl.getMethod(superclass,
				// method.getName(), method.getParameterTypes());
				// } catch (MethodNotFoundException e) {
				// // OK
				// }
				// boolean contains = mockedMethods.contains(superClassMethod);
				// if (superclass == null || superclass.equals(Object.class) ||
				// superClassMethod == null || !contains) {
				// return proxy.invokeSuper(obj, args);
				// } else {
				// return handler.invoke(obj, superClassMethod, args);
				// }
				// }
				//
				// return handler.invoke(obj, method, args);
				if (mockedMethods != null && !mockedMethods.contains(method)) {
					return proxy.invokeSuper(obj, args);
				}

				return handler.invoke(obj, method, args);
			}

			public InvocationHandler getHandler() {
				return handler;
			}

			public void setMockedMethods(Method... mockedMethods) {
				this.mockedMethods = new HashSet<Method>(Arrays.asList(mockedMethods));
			}
		};

		// Create the mock
		Enhancer enhancer = new Enhancer() {
			/**
			 * Filter all private constructors but do not check that there are
			 * some left
			 */
			@Override
			protected void filterConstructors(Class sc, List constructors) {
				CollectionUtils.filter(constructors, new VisibilityPredicate(sc, true));
			}
		};

		if (toMock.getSigners() != null) {
			enhancer.setNamingPolicy(SignedSupportingNamingPolicy.getInstance());
		}
		enhancer.setSuperclass(toMock);
		enhancer.setCallbackType(interceptor.getClass());

		Class mockClass = enhancer.createClass();
		Enhancer.registerCallbacks(mockClass, new Callback[] { interceptor });

		if (ClassExtensionHelper.getCurrentConstructorArgs() != null) {
			// Really instantiate the class
			ConstructorArgs args = ClassExtensionHelper.getCurrentConstructorArgs();
			Constructor cstr;
			try {
				// Get the constructor with the same params
				cstr = mockClass.getDeclaredConstructor(args.getConstructor().getParameterTypes());
			} catch (NoSuchMethodException e) {
				// Shouldn't happen, constructor is checked when ConstructorArgs
				// is instantiated
				// ///CLOVER:OFF
				throw new RuntimeException("Fail to find constructor for param types", e);
				// ///CLOVER:ON
			}
			T mock;
			try {
				cstr.setAccessible(true); // So we can call a protected
				// constructor
				mock = (T) cstr.newInstance(args.getInitArgs());
			} catch (InstantiationException e) {
				// ///CLOVER:OFF
				throw new RuntimeException("Failed to instantiate mock calling constructor", e);
				// ///CLOVER:ON
			} catch (IllegalAccessException e) {
				// ///CLOVER:OFF
				throw new RuntimeException("Failed to instantiate mock calling constructor", e);
				// ///CLOVER:ON
			} catch (InvocationTargetException e) {
				final Throwable targetException = e.getTargetException();
				if (targetException instanceof RuntimeException) {
					throw (RuntimeException) targetException;
				} else if (targetException instanceof Error) {
					throw (Error) targetException;
				} else {
					throw new RuntimeException("Failed to instantiate mock calling constructor: Exception in constructor", e);
				}
			}
			return mock;
		} else {
			// Do not call any constructor

			Factory mock;
			try {
				mock = (Factory) ClassInstantiatorFactory.getInstantiator().newInstance(mockClass);
			} catch (InstantiationException e) {
				// ///CLOVER:OFF
				throw new RuntimeException("Fail to instantiate mock for " + toMock + " on " + ClassInstantiatorFactory.getJVM() + " JVM");
				// ///CLOVER:ON
			}

			// This call is required. CGlib has some "magic code" making sure a
			// callback is used by only one instance of a given class. So only
			// the
			// instance created right after registering the callback will get
			// it.
			// However, this is done in the constructor which I'm bypassing to
			// allow class instantiation without calling a constructor.
			// Fortunately, the "magic code" is also called in getCallback which
			// is
			// why I'm calling it here mock.getCallback(0);
			mock.getCallback(0);

			return (T) mock;
		}
	}

	private void updateMethod(InvocationHandler objectMethodsFilter, Method correctMethod) {
		Field methodField = retrieveField(ObjectMethodsFilter.class, correctMethod.getName() + "Method");
		updateField(objectMethodsFilter, correctMethod, methodField);
	}

	private Field retrieveField(Class<?> clazz, String field) {
		try {
			return clazz.getDeclaredField(field);
		} catch (NoSuchFieldException e) {
			// ///CLOVER:OFF
			throw new InternalError("There must be some refactoring because the " + field + " field was there...");
			// ///CLOVER:ON
		}
	}

	private void updateField(Object instance, Object value, Field field) {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(instance, value);
		} catch (IllegalAccessException e) {
			// ///CLOVER:OFF
			throw new InternalError("Should be accessible since we set it ourselves");
			// ///CLOVER:ON
		}
		field.setAccessible(accessible);
	}
}
