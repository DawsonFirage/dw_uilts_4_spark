package com.dwsn.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.impl;

import com.dwsn.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.Shape;

public class Rectangle implements Shape {

    @Override
    public void draw() {
        System.out.println("Rectangle...");
    }

}
