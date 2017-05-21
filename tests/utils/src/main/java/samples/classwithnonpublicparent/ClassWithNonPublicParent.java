package samples.classwithnonpublicparent;

public class ClassWithNonPublicParent extends PackageProtectedClass {}

class PackageProtectedClass {
    public String getSomeStringFromPackageProtectedClass() {
        return "some string";
    }
}
