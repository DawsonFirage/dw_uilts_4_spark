package com.dwsn.bigdata.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.impl;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.Shape;

public class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Circle...");
    }
}
