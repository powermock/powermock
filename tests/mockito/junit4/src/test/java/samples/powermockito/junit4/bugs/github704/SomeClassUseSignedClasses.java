package samples.powermockito.junit4.bugs.github704;

import org.eclipse.core.runtime.FileLocator;

public class SomeClassUseSignedClasses {

    public static FileLocator getFileLocator(){
        return null;
    }

}
