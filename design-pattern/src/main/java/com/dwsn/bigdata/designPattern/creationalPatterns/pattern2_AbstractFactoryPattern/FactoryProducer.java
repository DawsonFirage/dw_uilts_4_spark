package com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.AbstractFactory;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.impl.ColorFactory;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.impl.ShapeFactory;

public class FactoryProducer {
    public static AbstractFactory getFactory(String choice) {
        if (choice.equalsIgnoreCase("SHAPE")){
            return new ShapeFactory();
        } else if (choice.equalsIgnoreCase("COLOR")) {
            return new ColorFactory();
        }
        return null;
    }
}
