package org.powermock.core;

/**
 * Utilities for types.
 */
public class TypeUtils {

	/**
	 * Get the default value for a type.
	 * 
	 * @param type
	 *            The type whose default value to get.
	 * @return The default return type of <code>type</code>.
	 */
	public static Object getDefaultValue(Class<?> type) {
		return getDefaultValue(type.getName());
	}

	/**
	 * Get the default value of a type with based on its fully-qualified name.
	 * 
	 * @param fullyQualifiedTypeName
	 *            The name of the type whose default value to get.
	 * @return The default value of <code>fullyQualifiedTypeName</code>.
	 */
	public static Object getDefaultValue(String fullyQualifiedTypeName) {
		if (fullyQualifiedTypeName == null) { // Void
			return "";
		} else if (fullyQualifiedTypeName.equals(String.class.getName())) {
			return "";
		} else if (fullyQualifiedTypeName.equals(byte.class.getName())) {
			return (byte) 0;
		} else if (fullyQualifiedTypeName.equals(int.class.getName())) {
			return 0;
		} else if (fullyQualifiedTypeName.equals(short.class.getName())) {
			return (short) 0;
		} else if (fullyQualifiedTypeName.equals(long.class.getName())) {
			return 0L;
		} else if (fullyQualifiedTypeName.equals(float.class.getName())) {
			return 0.0F;
		} else if (fullyQualifiedTypeName.equals(double.class.getName())) {
			return 0.0D;
		} else if (fullyQualifiedTypeName.equals(boolean.class.getName())) {
			return false;
		} else if (fullyQualifiedTypeName.equals(char.class.getName())) {
			return ' ';
		} else {
			return null;
		}
	}
}
