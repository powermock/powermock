package samples.annotationbased;

import samples.annotationbased.testannotations.RuntimeAnnotation;

@RuntimeAnnotation
public class AnnotatedClassDemo {
    public static boolean staticMethod() {
        return false;
    }
}
