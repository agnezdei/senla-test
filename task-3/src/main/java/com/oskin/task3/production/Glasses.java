package com.oskin.task3.production;

public class Glasses implements IProduct {
    private IProductPart frame;
    private IProductPart lenses;
    private IProductPart arms;
    
    @Override
    public void installFirstPart(IProductPart part) {
        this.frame = part;
        System.out.println("Установлен: " + part.getName());
    }
    
    @Override
    public void installSecondPart(IProductPart part) {
        this.lenses = part;
        System.out.println("Установлены: " + part.getName());
    }
    
    @Override
    public void installThirdPart(IProductPart part) {
        this.arms = part;
        System.out.println("Установлены: " + part.getName());
    }
    
    @Override
    public String getDescription() {
        return "Очки собраны! Состав: " + 
               (frame != null ? frame.getName() : "нет") + ", " +
               (lenses != null ? lenses.getName() : "нет") + ", " +
               (arms != null ? arms.getName() : "нет");
    }
}