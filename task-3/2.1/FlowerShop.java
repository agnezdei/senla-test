import java.util.ArrayList;
import java.util.List;

abstract class Flower {
    protected String name;
    protected double price;
    
    public Flower(String name, double price) {
        this.name = name;
        this.price = price;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    @Override
    public String toString() {
        return name + " - " + price + " руб.";
    }
}

class Rose extends Flower {
    public Rose() {
        super("Роза", 150.0);
    }
    
    public Rose(double customPrice) {
        super("Роза", customPrice);
    }
}

class Chrysanthemum extends Flower {
    public Chrysanthemum() {
        super("Хризантема", 80.0);
    }
}

class Gypsophila extends Flower {
    public Gypsophila() {
        super("Гипсофила", 60.0);
    }
}

class Hydrangea extends Flower {
    public Hydrangea() {
        super("Гортензия", 200.0);
    }
}

class Bouquet {
    private List<Flower> flowers = new ArrayList<>();

    public void addFlower(Flower flower) {
        flowers.add(flower);
    }
    
    public void addFlowers(Flower flower, int count) {
        for (int i = 0; i < count; i++) {
            flowers.add(flower);
        }
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (Flower flower : flowers) {
            total += flower.getPrice();
        }
        return total;
    }
    
    public void showComposition() {
        System.out.println("Состав букета:");
        for (Flower flower : flowers) {
            System.out.println("  " + flower);
        }
        System.out.printf("Общая стоимость: %.2f руб.\n", calculateTotalPrice());
    }
}

public class FlowerShop {
    public static void main(String[] args) {
        
        Bouquet bouquet = new Bouquet();
        
        bouquet.addFlowers(new Rose(), 3);
        bouquet.addFlowers(new Chrysanthemum(), 5);
        bouquet.addFlower(new Gypsophila());
        bouquet.addFlower(new Hydrangea());
        
        bouquet.showComposition();
    }
}