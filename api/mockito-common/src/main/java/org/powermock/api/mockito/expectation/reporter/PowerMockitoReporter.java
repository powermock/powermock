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

package org.powermock.api.mockito.expectation.reporter;

import org.mockito.exceptions.Reporter;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;

import static org.powermock.utils.StringJoiner.join;

/**
 * PowerMock reported for Mockito, which replace standard mockito message
 * to specific message for cases when PowerMock is used.
 */
public class PowerMockitoReporter extends Reporter {

    public void missingMethodInvocation() {
        throw new MissingMethodInvocationException(join(
                "when() requires an argument which has to be 'a method call on a mock'.",
                "For example:",
                "    when(mock.getArticles()).thenReturn(articles);",
                "Or 'a static method call on a prepared class`",
                "For example:",
                "    @PrepareForTest( { StaticService.class }) ",
                "    TestClass{",
                "       public void testMethod(){",
                "           PowerMockito.mockStatic(StaticService.class);",
                "           when(StaticService.say()).thenReturn(expected);",
                "       }",
                "    }",
                "",
                "Also, this error might show up because:",
                "1. inside when() you don't call method on mock but on some other object.",
                "2. inside when() you don't call static method, but class has not been prepared.",
                ""
        ));
    }

}
