package org.powermock.core.testlisteners;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.powermock.core.TypeUtils;
import org.powermock.core.spi.support.AbstractPowerMockTestListenerBase;
import org.powermock.reflect.Whitebox;
import org.powermock.tests.result.TestMethodResult;

/**
 * A test listener that automatically set all instance fields to their default
 * values after each test method. E.g. an object field is set to
 * <code>null</code>, an <code>int</code> field is set to 0 and so on.
 */
public class FieldDefaulter extends AbstractPowerMockTestListenerBase {

	@Override
	public void afterTestMethod(Object testInstance, Method method, Object[] arguments, TestMethodResult testResult) throws Exception {
		Set<Field> allFields = Whitebox.getAllFields(testInstance);
		for (Field field : allFields) {
			field.set(testInstance, TypeUtils.getDefaultValue(field.getType()));
		}
	}
}
