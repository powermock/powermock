/*
 *   Copyright 2016 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.powermock.api.mockito.internal.invocation;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.invocation.ArgumentsProcessor;
import org.mockito.internal.matchers.text.MatchersPrinter;
import org.mockito.internal.reporting.PrintSettings;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.List;

/**
 * We need to override the toString() in some classes because normally the toString
 * "method" is assembled by calling the "qualifiedName" method but
 * this is not possible in our case. The reason is that the
 * qualifiedName method does
 *
 * <pre>
 * new MockUtil().getMockName(mock)
 * </pre>
 *
 * which later will call the "isMockitoMock" method which will
 * return false and an exception will be thrown. The reason why
 * "isMockitoMock" returns false is that the mock is not created by
 * the Mockito CGLib Enhancer in case of static methods.
 */
public class ToStringGenerator {

    public String generate(Object mock, Method method, Object[] arguments) {
        final List<ArgumentMatcher> matcherList = ArgumentsProcessor.argumentsToMatchers(arguments);
        final PrintSettings printSettings = new PrintSettings();
        MatchersPrinter matchersPrinter = new MatchersPrinter();

        String methodName = Whitebox.getUnproxyType(mock).getName() + "." + method.getName();
        String invocation = methodName + matchersPrinter.getArgumentsLine(matcherList, printSettings);
        if (printSettings.isMultiline()
                || (!matcherList.isEmpty() && invocation.length() > Whitebox.<Integer> getInternalState(
                PrintSettings.class, "MAX_LINE_LENGTH"))) {
            return methodName + matchersPrinter.getArgumentsBlock(matcherList, printSettings);
        } else {
            return invocation;
        }
    }
}
