/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.modules.agent;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.regex.*;

final class AgentInitialization
{
    private static final Pattern JAR_REGEX = Pattern.compile(".*powermock-module-javaagent[-]?[.\\d+]*[-]?[A-Z]*.jar");

    void initializeAccordingToJDKVersion()  {
        String jarFilePath = discoverPathToJarFile();

        if (PowerMockAgent.jdk6OrLater) {
            new JDK6AgentLoader(jarFilePath).loadAgent();
        }
        else if ("1.5".equals(PowerMockAgent.javaSpecVersion)) {
            throw new IllegalStateException(
                    "PowerMock has not been initialized. Check that your Java 5 VM has been started with the -javaagent:" +
                            jarFilePath + " command line option.");
        }
        else {
            throw new IllegalStateException("PowerMock requires a Java 5 VM or later.");
        }
    }

    private String discoverPathToJarFile()
    {
        String jarFilePath = findPathToJarFileFromClasspath();

        if (jarFilePath == null) {
            // This can fail for a remote URL, so it is used as a fallback only:
            jarFilePath = getPathToJarFileContainingThisClass();
        }

        if (jarFilePath != null) {
            return jarFilePath;
        }

        throw new IllegalStateException(
                "No jar file with name ending in \"powermock-module-javaagent.jar\" or \"powermock-module-javaagent-nnn.jar\" (where \"nnn\" is a version number) " +
                        "found in the classpath");
    }

    private String findPathToJarFileFromClasspath()
    {
        String[] classPath = System.getProperty("java.class.path").split(File.pathSeparator);

        for (String cpEntry : classPath) {
            if (JAR_REGEX.matcher(cpEntry).matches()) {
                return cpEntry;
            }
        }

        return null;
    }

    private String getPathToJarFileContainingThisClass() {
        CodeSource codeSource = AgentInitialization.class.getProtectionDomain().getCodeSource();

        if (codeSource == null) {
            return null;
        }

        URI jarFileURI; // URI is needed to deal with spaces and non-ASCII characters

        try {
            jarFileURI = codeSource.getLocation().toURI();
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new File(jarFileURI).getPath();
    }
}
