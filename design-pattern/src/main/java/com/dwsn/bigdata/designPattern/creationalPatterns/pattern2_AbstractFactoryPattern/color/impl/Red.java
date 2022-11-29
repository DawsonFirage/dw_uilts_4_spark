package com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl;

import com.dwsn.bigdata.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;

public class Red implements Color {
    @Override
    public void fill() {
        System.out.println("Red Fill...");
    }
}
