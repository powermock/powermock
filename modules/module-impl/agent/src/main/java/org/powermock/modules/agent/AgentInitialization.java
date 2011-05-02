/*
 * The JMockit Testing Toolkit
 * Copyright (c) 2006-2011 Rogério Liesenfeld
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.powermock.modules.agent;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.regex.Pattern;

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
