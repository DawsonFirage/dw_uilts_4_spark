package com.dwsn.java;

public class Daughter {
    public static String name = "daughter";
    public static Integer age = 14;
    public static Father father = new Father();

    public void printAge() {
        father.printAge(age);
    }

    public void growUp() {
        father.growUp(age);
    }

    public void printName() {
        father.printName(name);
    }

}
