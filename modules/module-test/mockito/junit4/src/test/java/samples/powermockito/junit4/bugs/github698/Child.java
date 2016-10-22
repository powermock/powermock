package samples.powermockito.junit4.bugs.github698;

class Child extends Parent {

    @Override
    public String print() {
        String string = "";
        string += super.print();
        string += "Child";
        string += super.print();
        return string;
    }
}
