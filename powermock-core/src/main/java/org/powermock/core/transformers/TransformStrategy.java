/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.powermock.core.transformers;

/**
 * The enum provide information for {@link MockTransformer} have PowerMock is started via Runner(FactoryObject), Rule or JavaAgent
 */
public enum TransformStrategy {
    CLASSLOADER {
        @Override
        public boolean isClassloaderMode() {
            return true;
        }
    
        @Override
        public boolean isAgentMode() {
            return false;
        }
    },
    INST_REDEFINE {
        @Override
        public boolean isClassloaderMode() {
            return false;
        }
    
        @Override
        public boolean isAgentMode() {
            return true;
        }
    };
    
    /**
     * Check if this strategy is supported by class loader. It means that more byte code instrumenting are allowed: like adding constructor,
     * changeling method signature and so on
     * @return <code>true</code> if a strategy is supported by class loader.
     */
    public abstract boolean isClassloaderMode();
    
    /**
     * Check if this strategy is supported only by Java Agent. It means that lest byte code instrumenting are allowed and PowerMock should
     * avoid using some instrument things.
     * @return <code>true</code> if a strategy is supported only by Java Agent
     */
    public abstract boolean isAgentMode();
}
