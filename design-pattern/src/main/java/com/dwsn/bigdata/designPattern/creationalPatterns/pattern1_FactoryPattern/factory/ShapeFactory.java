package com.dwsn.bigdata.designPattern.creationalPatterns.pattern1_FactoryPattern.factory;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.Shape;
import com.dwsn.bigdata.designPattern.creationalPatterns.pattern1_FactoryPattern.shape.impl.*;

public class ShapeFactory {

    public Shape getShape(String shapeType) {
        if(shapeType == null){
            return null;
        }
        if(shapeType.equalsIgnoreCase("CIRCLE")){
            return new Circle();
        } else if(shapeType.equalsIgnoreCase("RECTANGLE")){
            return new Rectangle();
        } else if(shapeType.equalsIgnoreCase("SQUARE")){
            return new Square();
        }
        return null;
    }

}
