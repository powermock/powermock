package org.powermock.api.easymock.internal.invocationcontrol;

import org.powermock.core.spi.support.InvocationSubstitute;

import java.util.regex.Matcher;

public class NewInvocationControlAssertionError {
    public static void throwAssertionErrorForNewSubstitutionFailure(AssertionError oldError, Class<?> type) {
        /*
         * We failed to verify the new substitution mock. This happens when, for
         * example, the user has done something like
         * expectNew(MyClass.class).andReturn(myMock).times(3) when in fact an
         * instance of MyClass has been created less or more times than 3.
         */
        String message = oldError.getMessage();
        final String newSubstitutionMethodName = InvocationSubstitute.class.getDeclaredMethods()[0].getName();
        final String className = InvocationSubstitute.class.getSimpleName();
        message = message.replaceAll(className+"."+newSubstitutionMethodName, Matcher.quoteReplacement(type.getName()));
        message = message.replaceAll("method", "constructor");

        throw new AssertionError(message);
    }
}
