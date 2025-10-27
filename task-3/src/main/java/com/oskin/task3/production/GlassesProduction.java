package com.oskin.task3.production;

public class GlassesProduction {
    public static void main(String[] args) {
        ILineStep frameStep = new FrameStep();
        ILineStep lensesStep = new LensesStep();
        ILineStep armsStep = new ArmsStep();
        
        IAssemblyLine assemblyLine = new AssemblyLine(frameStep, lensesStep, armsStep);
        IProduct glasses = new Glasses();
        
        IProduct assembledGlasses = assemblyLine.assembleProduct(glasses);
        System.out.println("\nРезультат: " + assembledGlasses.getDescription());
    }
}