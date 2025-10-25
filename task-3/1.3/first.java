import java.util.Random;

public class first {
    public static void main(String[] args) {
        Random random = new Random();
        
        int num1 = random.nextInt(900) + 100;
        int num2 = random.nextInt(900) + 100;
        int num3 = random.nextInt(900) + 100;

        long combined = Long.parseLong(num1 + "" + num2);
        
        long difference = combined - num3;
        
        System.out.println("Сгенерированные числа:");
        System.out.println("Первое: " + num1);
        System.out.println("Второе: " + num2);
        System.out.println("Третье: " + num3);
        System.out.println("\nОбъединенное число: " + combined);
        System.out.println("Разница: " + combined + " - " + num3 + " = " + difference);
    }
}