package com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.impl;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.AbstractFactory;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.shape.Shape;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.shape.impl.*;

public class ShapeFactory extends AbstractFactory {
    @Override
    public Color getColor(String color) {
        return null;
    }

    @Override
    public Shape getShape(String shape) {
        if(shape == null){
            return null;
        }
        if(shape.equalsIgnoreCase("CIRCLE")){
            return new Circle();
        } else if(shape.equalsIgnoreCase("RECTANGLE")){
            return new Rectangle();
        } else if(shape.equalsIgnoreCase("SQUARE")){
            return new Square();
        }
        return null;
    }
}
