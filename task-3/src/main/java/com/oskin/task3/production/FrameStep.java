package com.oskin.task3.production;

public class FrameStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготавливается корпус очков...");
        return new Frame();
    }
}