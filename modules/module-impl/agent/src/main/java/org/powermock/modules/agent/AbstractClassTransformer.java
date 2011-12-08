package org.powermock.modules.agent;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractClassTransformer {
    
    protected static final List<String> STARTS_WITH_IGNORED = new LinkedList<String>();
    protected static final List<String> CONTAINS_IGNORED = new LinkedList<String>();

    static {
        STARTS_WITH_IGNORED.add("org/powermock");
        STARTS_WITH_IGNORED.add("org/junit");
        STARTS_WITH_IGNORED.add("org/mockito");
        STARTS_WITH_IGNORED.add("javassist");
        STARTS_WITH_IGNORED.add("org/objenesis");
        STARTS_WITH_IGNORED.add("junit");
        STARTS_WITH_IGNORED.add("org/hamcrest");
        STARTS_WITH_IGNORED.add("sun/");
        STARTS_WITH_IGNORED.add("$Proxy");

        CONTAINS_IGNORED.add("CGLIB$$");
        CONTAINS_IGNORED.add("$$PowerMock");
    }     
    
    protected boolean shouldIgnore(String className) {
        for (String ignore : STARTS_WITH_IGNORED) {
            if(className.startsWith(ignore)) {
                return true;
            }
        }
    
        for (String ignore : CONTAINS_IGNORED) {
            if(className.contains(ignore)) {
                return true;
            }
        }
        return false;
    }

}
