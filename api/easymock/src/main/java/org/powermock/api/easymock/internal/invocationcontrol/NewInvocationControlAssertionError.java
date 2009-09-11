package org.powermock.api.easymock.internal.invocationcontrol;

import java.util.regex.Matcher;

import org.powermock.core.spi.support.InvocationSubstitute;

public class NewInvocationControlAssertionError {
    public static void throwAssertionErrorForNewSubstitutionFailure(AssertionError oldError, Class<?> type) {
        /*
         * We failed to verify the new substitution mock. This happens when, for
         * example, the user has done something like
         * expectNew(MyClass.class).andReturn(myMock).times(3) when in fact an
         * instance of MyClass has been created less or more times than 3.
         */
        String message = oldError.getMessage();
        final String newSubsitutionMethodName = InvocationSubstitute.class.getDeclaredMethods()[0].getName();
        message = message.replaceAll(newSubsitutionMethodName, Matcher.quoteReplacement(type.getName()));
        message = message.replaceAll("method", "constructor");

        throw new AssertionError(message);
    }
}
