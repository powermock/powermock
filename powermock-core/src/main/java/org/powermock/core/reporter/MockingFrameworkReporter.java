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

package org.powermock.core.reporter;

/**
 * The instance of the interface is used to replace default mocking frameworks
 * exception message via message specific for PowerMock use-cases.
 */
public interface MockingFrameworkReporter {

    /**
     * Start replacing mocking frameworks exception message
     */
    void enable();

    /**
     * Stop replacing mocking frameworks exception message
     */
    void disable();

}
