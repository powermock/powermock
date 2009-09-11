package org.powermock.api.mockito.internal.invocationcontrol;

import java.util.regex.Matcher;

import org.powermock.core.spi.support.InvocationSubstitute;

public class NewInvocationControlAssertionError {
    private static final String ERROR_LOCATION_MARKER = "->";
    private static final String COLON_NEWLINE = ":\n";
    private static final String HERE_TEXT = "here:\n";
    private static final String UNDESIRED_INVOCATION_TEXT = " Undesired invocation:";

    public static void throwAssertionErrorForNewSubstitutionFailure(AssertionError oldError, Class<?> type) {
        /*
         * We failed to verify the new substitution mock. This happens when, for
         * example, the user has done something like
         * whenNew(MyClass.class).thenReturn(myMock).times(3) when in fact an
         * instance of MyClass has been created less or more times than 3.
         */
        String message = oldError.getMessage();
        final String newSubsitutionClassName = InvocationSubstitute.class.getSimpleName();
        final String newSubsitutionClassNameInMockito = newSubsitutionClassName.substring(0, 1).toLowerCase() + newSubsitutionClassName.substring(1);
        final String newSubsitutionMethodName = InvocationSubstitute.class.getDeclaredMethods()[0].getName();
        message = message.replaceAll(newSubsitutionClassNameInMockito + "." + newSubsitutionMethodName, Matcher.quoteReplacement(type.getName()));
        message = message.replaceAll("method", "constructor");

        /*
         * Temp fix: Remove powermock internal "at locations" (points to which
         * line the expectation went wrong in Mockito). We should try to find
         * the real ones instead
         */
        StringBuilder builder = removeFailureLocations(message);
        // Remove "Undesired invocation:"
        removeText(builder, UNDESIRED_INVOCATION_TEXT);

        removeAndReplaceText(builder, HERE_TEXT, ' ');

        removeAndReplaceText(builder, COLON_NEWLINE, ' ');

        throw new AssertionError(builder.toString().trim());
    }

    private static StringBuilder removeFailureLocations(String message) {
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        int indexOfBeginLocation = builder.indexOf(ERROR_LOCATION_MARKER);
        while (indexOfBeginLocation > 0) {
            int indexOfLocationEnd = builder.indexOf("\n", indexOfBeginLocation);
            builder.delete(indexOfBeginLocation, indexOfLocationEnd < 0 ? builder.length() : indexOfLocationEnd + 1);
            indexOfBeginLocation = builder.indexOf(ERROR_LOCATION_MARKER);
        }
        return builder;
    }

    private static void removeAndReplaceText(StringBuilder builder, String text, char appender) {
        int currentTextIndex = builder.indexOf(text);
        int previousTextIndex = 0;
        boolean isSingleConcat = true;
        while (currentTextIndex > 0) {
            previousTextIndex = currentTextIndex;
            builder.delete(currentTextIndex, currentTextIndex + text.length());
            currentTextIndex = builder.indexOf(text);
            if (isLastFinding(currentTextIndex) && !isSingleConcat) {
                builder.replace(builder.length(), builder.length(), ".");
            } else {
                builder.replace(previousTextIndex, previousTextIndex + 1, String.valueOf(builder.charAt(previousTextIndex)).toLowerCase());
                builder.insert(previousTextIndex, String.valueOf(appender));
                currentTextIndex++;
                isSingleConcat = false;
            }
        }
    }

    private static boolean isLastFinding(int index) {
        return index < 0;
    }

    private static void removeText(StringBuilder builder, String text) {
        int textIndex = builder.indexOf(text);
        while (textIndex > 0) {
            builder.delete(textIndex, textIndex + text.length());
            textIndex = builder.indexOf(text);
        }
    }
}
