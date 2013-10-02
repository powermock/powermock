/*
 * Copyright 2008 the original author or authors.
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
package samples.system;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Class used to demonstrate PowerMock's ability to mock system classes.
 */
public class SystemClassUser {

    public void threadSleep() throws InterruptedException {
        Thread.sleep(5000);
    }

    public String performEncode() throws UnsupportedEncodingException {
        return URLEncoder.encode("string", "enc");
    }

    public Process executeCommand() throws IOException {
        return Runtime.getRuntime().exec("command");
    }

    public String getSystemProperty() throws IOException {
        return System.getProperty("property");
    }

    public void doMoreComplicatedStuff() throws IOException {
        System.setProperty("nanoTime", Long.toString(System.nanoTime()));
    }

    public void copyProperty(String to, String from) throws IOException {
        System.setProperty(to, System.getProperty(from));
    }

    public String format(String one, String args) throws IOException {
        return String.format(one, args);
    }

    public URL newURL(String anUrl) throws MalformedURLException {
        return new URL(anUrl);
    }

    public void shuffleCollection(List<?> list) {
        Collections.shuffle(list);
    }

    public URLConnection useURL(URL url) throws IOException {
        return url.openConnection();
    }
     
    public InetAddress getLocalHost() throws IOException {
        return InetAddress.getLocalHost();
    }

    public String generatePerishableToken() {
        final UUID uuid = UUID.randomUUID();
        final String toString = uuid.toString();
        final String result = toString.replaceAll("-", "");
        return result;
	}
    
    public int lengthOf(StringBuilder to){
        // The trick here is the casting to CharSequence,
        // this is to test that the runtime type(StringBuilder) is checked for mocked calls and not
        // the compile-time type (CharSequence)
        return lengthOf((CharSequence) to);
	}
	
    private int lengthOf(CharSequence to){
        return to.length();
    }
}
