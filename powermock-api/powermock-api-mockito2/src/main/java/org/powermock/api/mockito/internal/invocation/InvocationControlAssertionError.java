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
package org.powermock.api.mockito.internal.invocation;

import org.powermock.core.spi.support.InvocationSubstitute;
import org.powermock.reflect.Whitebox;

import java.util.regex.Matcher;

public class InvocationControlAssertionError {
    private static final String AT = "at";
    private static final String ERROR_LOCATION_MARKER = "->";
    private static final String COLON_NEWLINE = ":\n";
    private static final String NEWLINE_POINT = "\n.";
    private static final String HERE_TEXT = "here:\n";
    private static final String UNDESIRED_INVOCATION_TEXT = " Undesired invocation:";
    private static final String POWER_MOCKITO_CLASS_NAME = "org.powermock.api.mockito.PowerMockito";

    public static void updateErrorMessageForVerifyNoMoreInteractions(AssertionError errorToUpdate) {
        /*
         * VerifyNoMoreInteractions failed, we need to update the error message.
         */
        String verifyNoMoreInteractionsInvocation = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement.getClassName().equals(POWER_MOCKITO_CLASS_NAME)
                        && stackTraceElement.getMethodName().equals("verifyNoMoreInteractions")) {
                final int invocationStackTraceIndex;
                if (stackTrace[i + 1].getClassName().equals(POWER_MOCKITO_CLASS_NAME)
                            && stackTrace[i + 1].getMethodName().equals("verifyZeroInteractions")) {
                    invocationStackTraceIndex = i + 2;
                } else {
                    invocationStackTraceIndex = i + 1;
                }
                verifyNoMoreInteractionsInvocation = stackTrace[invocationStackTraceIndex].toString();
            }
        }

        if (verifyNoMoreInteractionsInvocation == null) {
            // Something unexpected happened, just return
            return;
        }
        String message = errorToUpdate.getMessage();
        StringBuilder builder = new StringBuilder();
        builder.append(message);
        final int indexOfFirstAt = message.indexOf(AT);
        final int startOfVerifyNoMoreInteractionsInvocation = indexOfFirstAt + AT.length() + 1;
        final int endOfVerifyNoMoreInteractionsInvocation = message.indexOf('\n', indexOfFirstAt + AT.length());
        builder.replace(startOfVerifyNoMoreInteractionsInvocation, endOfVerifyNoMoreInteractionsInvocation,
                        verifyNoMoreInteractionsInvocation);
        builder.delete(builder.indexOf("\n", endOfVerifyNoMoreInteractionsInvocation + 1), builder.lastIndexOf("\n"));
        Whitebox.setInternalState(errorToUpdate, "detailMessage", builder.toString());
    }

    public static void updateErrorMessageForMethodInvocation(AssertionError errorToUpdate) {
        /*
         * We failed to verify the new substitution mock. This happens when, for
         * example, the user has done something like
         * whenNew(MyClass.class).thenReturn(myMock).times(3) when in fact an
         * instance of MyClass has been created less or more times than 3.
         */
        Whitebox.setInternalState(errorToUpdate, "detailMessage", "\n" + changeMessageContent(errorToUpdate.getMessage()));
    }

    public static void throwAssertionErrorForNewSubstitutionFailure(AssertionError oldError, Class<?> type) {
        /*
         * We failed to verify the new substitution mock. This happens when, for
         * example, the user has done something like
         * whenNew(MyClass.class).thenReturn(myMock).times(3) when in fact an
         * instance of MyClass has been created less or more times than 3.
         */
        final String newSubstitutionClassName = InvocationSubstitute.class.getSimpleName();
        final String newSubstitutionClassNameInMockito = newSubstitutionClassName.substring(0, 1).toLowerCase()
                                                                + newSubstitutionClassName.substring(1);
        String message = oldError.getMessage();
        final String newSubstitutionMethodName = InvocationSubstitute.class.getDeclaredMethods()[0].getName();
        message = message.replaceAll(newSubstitutionClassNameInMockito + "." + newSubstitutionMethodName, Matcher
                                                                                                                .quoteReplacement(type.getName()));
        message = message.replaceAll("method", "constructor");
        throw new AssertionError(changeMessageContent(message));
    }

    private static String changeMessageContent(String message) {
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
    
        return builder.toString().trim();
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
        int previousTextIndex;
        boolean isSingleConcat = true;
    
        while (currentTextIndex > 0) {
            previousTextIndex = currentTextIndex;
            builder.delete(currentTextIndex, currentTextIndex + text.length());
            currentTextIndex = builder.indexOf(text);
            
            final int length = builder.length();
            
            if (isLastFinding(currentTextIndex) && !isSingleConcat) {
                final int start = builder.charAt(length - 1) == '\n' ? length - 1 : length;
                builder.replace(start, length, ".");
            } else {
                final int end = previousTextIndex < length ? previousTextIndex + 1 : length;
                builder.replace(
                    previousTextIndex, end,
                    String.valueOf(builder.charAt(previousTextIndex)).toLowerCase()
                );
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
