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

package org.powermock.modules.agent.support;

import org.powermock.core.agent.JavaAgentClassRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Basic not thread-safety implementation of  the  {@link JavaAgentClassRegister}
 */
public class JavaAgentClassRegisterImpl implements JavaAgentClassRegister {

    private final Map<ClassLoader, Set<String>> modifiedClasses;

    public JavaAgentClassRegisterImpl() {
        modifiedClasses = new HashMap<ClassLoader, Set<String>>();
    }

    @Override
    public boolean isModifiedByAgent(ClassLoader classLoader, String className) {
        return modifiedClasses.containsKey(classLoader) && modifiedClasses.get(classLoader).contains(className);
    }

    @Override
    public void registerClass(ClassLoader loader, String className) {

        Set<String> names = modifiedClasses.get(loader);
        if (names == null){
            names = new HashSet<String>();
            modifiedClasses.put(loader, names);
        }

        names.add(className);

    }

    @Override
    public void clear() {
        modifiedClasses.clear();
    }
}
