package com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;

public class Blue implements Color {
    @Override
    public void fill() {
        System.out.println("Blue Fill...");
    }
}
