/*
 *  Copyright (c) 2007 Mockito contributors
 *  This program is made available under the terms of the MIT License.
 */
package org.powermock.api.mockito.repackaged.cglib.core;

import org.powermock.api.mockito.repackaged.asm.ClassAdapter;
import org.powermock.api.mockito.repackaged.asm.ClassReader;

import java.util.ArrayList;
import java.util.List;

// TODO: optimize (ClassReader buffers entire class before accept)
public class ClassNameReader {
    private static final EarlyExitException EARLY_EXIT = new EarlyExitException();

    private ClassNameReader() {
    }

    public static String getClassName(ClassReader r) {

        return getClassInfo(r)[0];

    }
    
    public static String[] getClassInfo(ClassReader r) {
        final List array = new ArrayList();
        try {
            r.accept(new ClassAdapter(null) {
                public void visit(int version,
                                  int access,
                                  String name,
                                  String signature,
                                  String superName,
                                  String[] interfaces) {
                    array.add( name.replace('/', '.') );
                    if(superName != null){
                      array.add( superName.replace('/', '.') );
                    }
                    for(int i = 0; i < interfaces.length; i++  ){
                       array.add( interfaces[i].replace('/', '.') );
                    }

                    throw EARLY_EXIT;
                }
            }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (EarlyExitException e) { }

        return (String[])array.toArray( new String[]{} );
    }
    
    private static class EarlyExitException extends RuntimeException { }
}
