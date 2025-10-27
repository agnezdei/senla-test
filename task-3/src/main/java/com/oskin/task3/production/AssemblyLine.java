package com.oskin.task3.production;

public class AssemblyLine implements IAssemblyLine {
    private ILineStep frameStep;
    private ILineStep lensesStep;
    private ILineStep armsStep;
    
    public AssemblyLine(ILineStep frameStep, ILineStep lensesStep, ILineStep armsStep) {
        this.frameStep = frameStep;
        this.lensesStep = lensesStep;
        this.armsStep = armsStep;
    }
    
    @Override
    public IProduct assembleProduct(IProduct product) {
        System.out.println("=== Начало сборки очков ===");
        
        System.out.println("\nШаг 1:");
        IProductPart frame = frameStep.buildProductPart();
        product.installFirstPart(frame);
        
        System.out.println("\nШаг 2:");
        IProductPart lenses = lensesStep.buildProductPart();
        product.installSecondPart(lenses);
        
        System.out.println("\nШаг 3:");
        IProductPart arms = armsStep.buildProductPart();
        product.installThirdPart(arms);
        
        System.out.println("\n=== Сборка завершена ===");
        return product;
    }
}