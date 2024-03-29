package com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.impl;

import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl.*;
import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl.Red;
import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.AbstractFactory;
import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.shape.Shape;
import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl.Blue;
import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl.Green;

public class ColorFactory extends AbstractFactory {
    @Override
    public Color getColor(String color) {
        if(color == null){
            return null;
        }
        if(color.equalsIgnoreCase("RED")){
            return new Red();
        } else if(color.equalsIgnoreCase("GREEN")){
            return new Green();
        } else if(color.equalsIgnoreCase("BLUE")){
            return new Blue();
        }
        return null;
    }

    @Override
    public Shape getShape(String shape) {
        return null;
    }
}
