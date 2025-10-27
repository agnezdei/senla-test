package com.oskin.task3.production;

public class LensesStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Шлифуются и устанавливаются линзы...");
        return new Lenses();
    }
}