package com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.factory.AbstractFactory;

public class App {

    public static void main(String[] args) {
        AbstractFactory colorFactory = FactoryProducer.getFactory("COLOR");
        Color color = colorFactory.getColor("RED");
        color.fill();
    }

}
