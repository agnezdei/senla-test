package com.oskin.task2;

import java.util.ArrayList;
import java.util.List;

public class Bouquet {
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