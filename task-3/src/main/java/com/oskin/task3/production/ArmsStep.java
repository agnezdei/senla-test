package com.oskin.task3.production;

public class ArmsStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготавливаются дужки...");
        return new Arms();
    }
}