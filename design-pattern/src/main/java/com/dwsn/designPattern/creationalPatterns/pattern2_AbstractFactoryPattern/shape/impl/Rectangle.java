package com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.shape.impl;

import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.shape.Shape;

public class Rectangle implements Shape {

    @Override
    public void draw() {
        System.out.println("Rectangle...");
    }

}
