package com.oskin.task2;

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