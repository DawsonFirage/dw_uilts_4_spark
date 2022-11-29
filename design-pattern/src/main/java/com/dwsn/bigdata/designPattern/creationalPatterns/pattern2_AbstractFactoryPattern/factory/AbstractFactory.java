package com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.shape.Shape;

public abstract class AbstractFactory {
    public abstract Color getColor(String color);
    public abstract Shape getShape(String shape);
}
