package com.dwsn.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.impl;

import com.dwsn.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.Shape;

public class Square implements Shape {

    @Override
    public void draw() {
        System.out.println("Square...");
    }

}
