package com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.impl;

import com.dwsn.designPattern.creationalPatterns.pattern2_AbstractFactoryPattern.color.Color;

public class Green implements Color {
    @Override
    public void fill() {
        System.out.println("Green Fill...");
    }
}
