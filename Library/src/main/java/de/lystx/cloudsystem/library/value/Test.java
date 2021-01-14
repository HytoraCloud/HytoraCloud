package de.lystx.cloudsystem.library.value;

public class Test {

    public static void main(String[] args) {
        ValueObject valueObject = new ValueObject();
        valueObject.append("Lystx", "name_lystx");

        System.out.println(valueObject.toString());
    }
}
