package com.dwsn.java;

public class Test {

    public static void main(String[] args) {
        Son son = new Son();
        Daughter daughter = new Daughter();

        System.out.print("Son:");
        son.printAge();

        System.out.print("Daughter:");
        daughter.printAge();
    }

}
