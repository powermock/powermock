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
package org.powermock.reflect.internal;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.exceptions.TooManyMethodsFoundException;
import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Pins down how {@link WhiteboxImpl} makes members accessible under the Java 9+ module system
 * ({@code trySetAccessible}). {@code java.util.concurrent} is deliberately used as the "not opened" package:
 * the test JVM may open java.lang, java.util, java.lang.reflect and java.awt.font, but never java.util.concurrent.
 * Requirement numbers (R1..R5) refer to the specification of the change.
 */
@SuppressWarnings("deprecation") // AccessibleObject.isAccessible() is the only accessor that exists on Java 8
public class WhiteboxImplModuleAccessTest {

    private static final boolean JAVA_9_PLUS = hasTrySetAccessible();

    /**
     * Whether the runtime actually encapsulates JDK internals: {@code setAccessible(true)} on a private field of
     * {@code java.util.concurrent.ConcurrentHashMap} throws {@code InaccessibleObjectException}. False on Java 8,
     * on JDK 9-15, where illegal reflective access is permitted by default, and whenever {@code java.util.concurrent} is opened.
     */
    private static final boolean JDK_INTERNALS_CLOSED = jdkInternalsClosed();

    private static boolean jdkInternalsClosed() {
        try {
            ConcurrentHashMap.class.getDeclaredField("sizeCtl").setAccessible(true);
            return false;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        } catch (RuntimeException e) {
            // InaccessibleObjectException does not exist on Java 8, so match it by name
            if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
                return true;
            }
            throw e;
        }
    }

    /** The R1 expectations only hold where the runtime keeps java.util.concurrent closed (a runtime property, not a version). */
    private static void assumeJdkInternalsClosed() {
        assumeTrue("JDK internals are open in this runtime", JDK_INTERNALS_CLOSED);
    }

    /** Feature version of the running JDK ("1.8" -> 8, "17" -> 17); Java 8 has no Runtime.version(). */
    private static int javaFeatureVersion() {
        String version = System.getProperty("java.specification.version");
        return Integer.parseInt(version.startsWith("1.") ? version.substring(2) : version);
    }

    private static boolean hasTrySetAccessible() {
        try {
            AccessibleObject.class.getMethod("trySetAccessible");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ R1: JDK members in unopened packages

    @Test
    public void r1_getAllMethods_on_unopened_jdk_class_does_not_throw_and_leaves_private_methods_inaccessible() {
        assumeJdkInternalsClosed();
        Method[] methods = WhiteboxImpl.getAllMethods(ConcurrentHashMap.class);

        Method tryPresize = findByName(methods, "tryPresize");
        assertNotNull("private method must be returned", tryPresize);
        assertFalse(tryPresize.isAccessible());
    }

    @Test
    public void r1_getMethods_by_name_on_unopened_jdk_class_returns_inaccessible_private_method() {
        assumeJdkInternalsClosed();
        Method[] methods = WhiteboxImpl.getMethods(ConcurrentHashMap.class, "tryPresize");

        assertEquals(1, methods.length);
        assertFalse(methods[0].isAccessible());
    }

    @Test
    public void r1_getMethod_by_parameter_types_on_unopened_jdk_class_returns_inaccessible_private_method() {
        assumeJdkInternalsClosed();
        // private final void fullAddCount(long, boolean) is the only declared (long, boolean) method
        Method fullAddCount = WhiteboxImpl.getMethod(ConcurrentHashMap.class, long.class, boolean.class);

        assertEquals("fullAddCount", fullAddCount.getName());
        assertFalse(fullAddCount.isAccessible());
    }

    @Test
    public void r1_getField_on_unopened_jdk_class_returns_inaccessible_private_field() {
        assumeJdkInternalsClosed();
        Field sizeCtl = WhiteboxImpl.getField(ConcurrentHashMap.class, "sizeCtl");

        assertEquals("sizeCtl", sizeCtl.getName());
        assertFalse(sizeCtl.isAccessible());
    }

    @Test
    public void r1_getAllPublicMethods_on_jdk_interface_does_not_throw() {
        // interfaces go through getAllPublicMethods; public members are always accessible
        Method[] methods = WhiteboxImpl.getMethods(java.util.concurrent.ConcurrentMap.class, "putIfAbsent");
        assertEquals(1, methods.length);
        assertTrue(methods[0].isAccessible());
    }

    // R1 + setInternalState paths: these run on every JDK. On Java 9+ they must fall back to Unsafe for the
    // inaccessible field instead of throwing; on Java 8 they set the (accessible) field as before.

    @Test
    public void r1_setInternalState_by_name_sets_private_field_of_unopened_jdk_class() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap.KeySetView<String, String> otherView = new ConcurrentHashMap<String, String>().keySet();

        WhiteboxImpl.setInternalState(map, "keySet", otherView);

        assertSame(otherView, map.keySet());
    }

    @Test
    public void r1_setInternalState_by_name_and_where_sets_private_field_of_unopened_jdk_class() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap.KeySetView<String, String> otherView = new ConcurrentHashMap<String, String>().keySet();

        WhiteboxImpl.setInternalState(map, "keySet", otherView, ConcurrentHashMap.class);

        assertSame(otherView, map.keySet());
    }

    @Test
    public void r1_setInternalState_by_field_type_uses_findFieldOrThrowException_on_unopened_jdk_class() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap.KeySetView<String, String> otherView = new ConcurrentHashMap<String, String>().keySet();

        WhiteboxImpl.setInternalState(map, ConcurrentHashMap.KeySetView.class, otherView, ConcurrentHashMap.class);

        assertSame(otherView, map.keySet());
    }

    @Test
    public void r1_setInternalState_on_static_final_field_of_unopened_jdk_class_uses_unsafe() throws Exception {
        // NCPU is initialised with availableProcessors(); writing the same value back keeps the JDK intact.
        int ncpu = Runtime.getRuntime().availableProcessors();
        Field field = ConcurrentHashMap.class.getDeclaredField("NCPU");
        Unsafe unsafe = unsafe();
        assertEquals(ncpu, unsafe.getInt(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field)));

        WhiteboxImpl.setInternalState(ConcurrentHashMap.class, "NCPU", ncpu);

        assertEquals(ncpu, unsafe.getInt(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field)));
    }

    // ------------------------------------------------------------------ R2: user classes unchanged

    @Test
    public void r2_getAllMethods_makes_private_methods_of_user_class_accessible_and_invocable() throws Exception {
        Method secret = null;
        for (Method method : WhiteboxImpl.getAllMethods(UserClass.class)) {
            if (method.equals(UserClass.class.getDeclaredMethod("secret", String.class))) {
                secret = method;
            }
        }

        assertNotNull(secret);
        assertTrue(secret.isAccessible());
        assertEquals("secret:x", secret.invoke(new UserClass(), "x"));
    }

    @Test
    public void r2_getMethods_by_name_makes_private_user_method_accessible() throws Exception {
        Method[] methods = WhiteboxImpl.getMethods(UserClass.class, "secret");

        assertEquals("both overloads", 2, methods.length);
        for (Method method : methods) {
            assertTrue(method.isAccessible());
        }
    }

    @Test
    public void r2_getMethod_by_parameter_types_makes_private_user_method_accessible() throws Exception {
        Method method = WhiteboxImpl.getMethod(UserClass.class, String.class);

        assertEquals("secret", method.getName());
        assertTrue(method.isAccessible());
    }

    @Test
    public void r2_whitebox_invokes_private_method_of_user_class() throws Exception {
        assertEquals("secret:z", Whitebox.invokeMethod(new UserClass(), "secret", "z"));
    }

    @Test
    public void r2_getField_makes_private_user_field_accessible_and_readable() throws Exception {
        UserClass instance = new UserClass();
        Field field = WhiteboxImpl.getField(UserClass.class, "hidden");

        assertTrue(field.isAccessible());
        assertEquals("initial", field.get(instance));
    }

    @Test
    public void r2_whitebox_reads_and_writes_private_user_fields() {
        UserClass instance = new UserClass();

        Whitebox.setInternalState(instance, "hidden", "changed");
        assertEquals("changed", Whitebox.getInternalState(instance, "hidden"));

        Whitebox.setInternalState(instance, "hidden", "again", UserClass.class);
        assertEquals("again", Whitebox.getInternalState(instance, "hidden"));

        Whitebox.setInternalState(instance, Integer.class, 7, UserClass.class);
        assertEquals(Integer.valueOf(7), Whitebox.getInternalState(instance, "number"));
        assertEquals(Integer.valueOf(7), Whitebox.getInternalState(instance, Integer.class, UserClass.class));
    }

    @Test
    public void r2_setInternalState_on_final_user_field_uses_unsafe_path() {
        UserClass instance = new UserClass();

        Whitebox.setInternalState(instance, "finalValue", new StringBuilder("replaced"));

        assertEquals("replaced", instance.finalValue().toString());
    }

    // ------------------------------------------------------------------ R3: inaccessible members are kept

    @Test
    public void r3_getAllMethods_keeps_every_declared_method_of_unopened_jdk_class() {
        Set<Method> result = new HashSet<Method>(Arrays.asList(WhiteboxImpl.getAllMethods(ConcurrentHashMap.class)));

        for (Method declared : ConcurrentHashMap.class.getDeclaredMethods()) {
            if (!"finalize".equals(declared.getName())) {
                assertTrue("missing " + declared, result.contains(declared));
            }
        }
    }

    @Test
    public void r3_getAllMethods_keeps_inaccessible_methods_in_the_array() {
        assumeTrue(JAVA_9_PLUS);
        List<Method> inaccessible = new ArrayList<Method>();
        for (Method method : WhiteboxImpl.getAllMethods(ConcurrentHashMap.class)) {
            if (!method.isAccessible()) {
                inaccessible.add(method);
            }
        }
        assertFalse("inaccessible private methods must stay in the array", inaccessible.isEmpty());
    }

    @Test
    public void r3_getMethods_with_several_names_finds_all_private_candidates() {
        Method[] methods = WhiteboxImpl.getMethods(ConcurrentHashMap.class, "readObject", "writeObject");

        Set<String> names = new HashSet<String>();
        for (Method method : methods) {
            names.add(method.getName());
        }
        assertEquals(new HashSet<String>(Arrays.asList("readObject", "writeObject")), names);
    }

    @Test
    public void r3_getMethods_with_overloads_on_unopened_jdk_class_returns_all_overloads() {
        // Declared twice in ConcurrentHashMap: remove(Object) and remove(Object, Object)
        Method[] methods = WhiteboxImpl.getMethods(ConcurrentHashMap.class, "remove");
        int declaredRemoves = 0;
        for (Method method : methods) {
            if (method.getDeclaringClass() == ConcurrentHashMap.class) {
                declaredRemoves++;
            }
        }
        assertTrue("expected both overloads, got " + Arrays.toString(methods), declaredRemoves >= 2);
    }

    @Test
    public void r3_getMethod_by_parameter_types_still_reports_too_many_methods_including_inaccessible_ones() {
        try {
            // get(Object), remove(Object), containsKey(Object), ..., static comparableClassFor(Object)
            WhiteboxImpl.getMethod(ConcurrentHashMap.class, Object.class);
            fail("expected TooManyMethodsFoundException");
        } catch (TooManyMethodsFoundException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("comparableClassFor"));
        }
    }

    @Test
    public void r3_findMethod_resolves_private_overload_by_argument() throws Exception {
        assertEquals("secret:1", Whitebox.invokeMethod(new UserClass(), "secret", 1));
        assertEquals("secret:a", Whitebox.invokeMethod(new UserClass(), "secret", "a"));
    }

    // ------------------------------------------------------------------ R4: Java 8 unchanged

    @Test
    public void r4_findTrySetAccessible_is_null_exactly_on_java_8() throws Exception {
        Method found = (Method) privateStatic("findTrySetAccessible").invoke(null);
        if (JAVA_9_PLUS) {
            assertEquals(AccessibleObject.class.getMethod("trySetAccessible"), found);
        } else {
            assertNull(found);
        }
    }

    @Test
    public void r4_on_java_8_trySetAccessible_calls_setAccessible_true() throws Exception {
        RecordingAccessibleObject object = new RecordingAccessibleObject();

        Object result = privateStatic("trySetAccessible", AccessibleObject.class).invoke(null, object);

        assertEquals(Boolean.TRUE, result);
        if (!JAVA_9_PLUS) {
            assertEquals(Arrays.asList(Boolean.TRUE), object.calls);
        }
    }

    @Test
    public void r4_on_java_8_every_method_of_a_jdk_class_is_made_accessible() {
        Method[] methods = WhiteboxImpl.getAllMethods(ConcurrentHashMap.class);
        if (!JAVA_9_PLUS) {
            // every declared member went through setAccessible(true); inherited public copies from
            // getMethods() are added without it (unchanged behaviour), hence the declaring-class filter
            for (Method method : methods) {
                if (method.getDeclaringClass() == ConcurrentHashMap.class) {
                    assertTrue("not accessible on Java 8: " + method, method.isAccessible());
                }
            }
            assertTrue(WhiteboxImpl.getField(ConcurrentHashMap.class, "sizeCtl").isAccessible());
        }
        for (Method method : methods) {
            if (method.getDeclaringClass() == ConcurrentHashMap.class && java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                assertTrue("public JDK method must be accessible: " + method, method.isAccessible());
            }
        }
    }

    // ------------------------------------------------------------------ R5: nothing is swallowed

    @Test
    public void r5_security_exception_from_trySetAccessible_propagates() throws Exception {
        final SecurityException failure = new SecurityException("denied by test");
        // resolved before the SecurityManager is armed, so only WhiteboxImpl's own access check can fail
        final Method trySetAccessible = privateStatic("trySetAccessible", AccessibleObject.class);
        final AccessibleObject member = UserClass.class.getDeclaredField("hidden");
        Throwable thrown = withAccessCheckFailure(failure, new Callable() {
            public void call() throws Throwable {
                trySetAccessible.invoke(null, member);
            }
        });
        assertSame(failure, unwrapReflection(thrown));
    }

    @Test
    public void r5_error_from_trySetAccessible_propagates_unwrapped() throws Exception {
        final TestError failure = new TestError();
        // resolved before the SecurityManager is armed, so only WhiteboxImpl's own access check can fail
        final Method trySetAccessible = privateStatic("trySetAccessible", AccessibleObject.class);
        final AccessibleObject member = UserClass.class.getDeclaredMethod("secret", String.class);
        Throwable thrown = withAccessCheckFailure(failure, new Callable() {
            public void call() throws Throwable {
                trySetAccessible.invoke(null, member);
            }
        });
        assertSame(failure, unwrapReflection(thrown));
    }

    @Test
    public void r5_security_exception_propagates_from_getField() throws Exception {
        final SecurityException failure = new SecurityException("denied by test");
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.getField(UserClass.class, "hidden");
            }
        }));
    }

    @Test
    public void r5_security_exception_propagates_from_getAllMethods() throws Exception {
        final SecurityException failure = new SecurityException("denied by test");
        // class used only here, so allClassMethodsCache cannot answer without making members accessible
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.getAllMethods(UncachedUserClass.class);
            }
        }));
    }

    @Test
    public void r5_error_propagates_from_getMethods_and_getMethod() throws Exception {
        final TestError failure = new TestError();
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.getMethod(UserClass.class, String.class);
            }
        }));
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.getMethods(java.util.concurrent.ConcurrentMap.class, "putIfAbsent");
            }
        }));
    }

    @Test
    public void r5_security_exception_propagates_from_setInternalState() throws Exception {
        final SecurityException failure = new SecurityException("denied by test");
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.setInternalState(new UserClass(), "hidden", "x");
            }
        }));
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.setInternalState(new UserClass(), "hidden", "x", UserClass.class);
            }
        }));
        assertSame(failure, withAccessCheckFailure(failure, new Callable() {
            public void call() {
                WhiteboxImpl.setInternalState(new UserClass(), Integer.class, 1, UserClass.class);
            }
        }));
    }

    // ------------------------------------------------------------------ helpers

    private interface Callable {
        void call() throws Throwable;
    }

    private static class TestError extends Error {
    }

    /**
     * Runs {@code action} with a SecurityManager that fails the "suppressAccessChecks" check (which both
     * {@code setAccessible(true)} on Java 8 and {@code trySetAccessible()} on Java 9+ perform) on this thread
     * only, and returns what the action threw.
     */
    private static Throwable withAccessCheckFailure(final Throwable failure, Callable action) throws Exception {
        // JEP 411: the SecurityManager is deprecated for removal and cannot be installed at runtime on JDK 18+
        // (System.setSecurityManager throws UnsupportedOperationException). A stub AccessibleObject cannot replace it:
        // trySetAccessible() is final and only fails through the SecurityManager's suppressAccessChecks check.
        assumeTrue("System.setSecurityManager is unsupported on JDK 18+ (JEP 411)", javaFeatureVersion() <= 17);
        final Thread testThread = Thread.currentThread();
        SecurityManager previous = System.getSecurityManager();
        final boolean[] armed = {true};
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                if (armed[0] && Thread.currentThread() == testThread && perm instanceof ReflectPermission
                        && "suppressAccessChecks".equals(perm.getName())) {
                    if (failure instanceof Error) {
                        throw (Error) failure;
                    }
                    throw (RuntimeException) failure;
                }
            }

            @Override
            public void checkPermission(Permission perm, Object context) {
                checkPermission(perm);
            }
        });
        try {
            action.call();
            return null;
        } catch (Throwable t) {
            return t;
        } finally {
            armed[0] = false;
            System.setSecurityManager(previous);
        }
    }

    private static Throwable unwrapReflection(Throwable thrown) {
        return thrown instanceof InvocationTargetException ? thrown.getCause() : thrown;
    }

    private static Method privateStatic(String name, Class<?>... parameterTypes) throws Exception {
        Method method = WhiteboxImpl.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    private static Method findByName(Method[] methods, String name) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    private static Unsafe unsafe() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (Unsafe) field.get(null);
    }

    /** An AccessibleObject that records setAccessible calls (only reached on Java 8). */
    private static class RecordingAccessibleObject extends AccessibleObject {
        final List<Boolean> calls = new ArrayList<Boolean>();

        @Override
        public void setAccessible(boolean flag) {
            calls.add(flag);
            super.setAccessible(flag);
        }
    }

    @SuppressWarnings("unused")
    private static class UserClass {
        private String hidden = "initial";
        private Integer number = 0;
        private final StringBuilder finalValue = new StringBuilder("original");

        private String secret(String value) {
            return "secret:" + value;
        }

        private String secret(int value) {
            return "secret:" + value;
        }

        StringBuilder finalValue() {
            return finalValue;
        }
    }

    @SuppressWarnings("unused")
    private static class UncachedUserClass {
        private void onlyHere() {
        }
    }
}
