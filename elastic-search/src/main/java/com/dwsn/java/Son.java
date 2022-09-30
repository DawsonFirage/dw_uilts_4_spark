package com.dwsn.java;

public class Son {
    public static String name = "son";
    public static Integer age = 18;
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
