interface IProductPart {
    String getName();
}

interface ILineStep {
    IProductPart buildProductPart();
}

interface IProduct {
    void installFirstPart(IProductPart part);
    void installSecondPart(IProductPart part);
    void installThirdPart(IProductPart part);
    String getDescription();
}

interface IAssemblyLine {
    IProduct assembleProduct(IProduct product);
}



class Frame implements IProductPart {
    @Override
    public String getName() {
        return "Корпус очков";
    }
}

class Lenses implements IProductPart {
    @Override
    public String getName() {
        return "Линзы";
    }
}

class Arms implements IProductPart {
    @Override
    public String getName() {
        return "Дужки";
    }
}



class FrameStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготавливается корпус очков...");
        return new Frame();
    }
}

class LensesStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Шлифуются и устанавливаются линзы...");
        return new Lenses();
    }
}

class ArmsStep implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Изготавливаются дужки...");
        return new Arms();
    }
}



class Glasses implements IProduct {
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



class AssemblyLine implements IAssemblyLine {
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
        
        System.out.println("\nШаг 1:");
        IProductPart frame = frameStep.buildProductPart();
        product.installFirstPart(frame);
        
        System.out.println("\nШаг 2:");
        IProductPart lenses = lensesStep.buildProductPart();
        product.installSecondPart(lenses);
        
        System.out.println("\nШаг 3:");
        IProductPart arms = armsStep.buildProductPart();
        product.installThirdPart(arms);
        
        return product;
    }
}



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