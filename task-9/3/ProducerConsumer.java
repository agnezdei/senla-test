import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ProducerConsumer {
    private static final int BUFFER_SIZE = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();
    
    public static void main(String[] args) throws InterruptedException {
        Thread producer = new Thread(new Producer(), "Производитель");
        Thread consumer = new Thread(new Consumer(), "Потребитель");
        
        producer.start();
        consumer.start();
        
        // Даем поработать 10 секунд
        Thread.sleep(10000);
        
        // Останавливаем потоки
        producer.interrupt();
        consumer.interrupt();
        
        producer.join();
        consumer.join();
        
        System.out.println("Программа завершена.");
    }
    
    static class Producer implements Runnable {
        private final Random random = new Random();
        private int counter = 0;
        
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    produce();
                    Thread.sleep(random.nextInt(500)); // Имитация работы
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void produce() throws InterruptedException {
            synchronized (buffer) {
                // Ждем, пока освободится место в буфере
                while (buffer.size() == BUFFER_SIZE) {
                    System.out.println(Thread.currentThread().getName() + ": Буфер полон, жду...");
                    buffer.wait();
                }
                
                int value = ++counter;
                buffer.add(value);
                System.out.println(Thread.currentThread().getName() + 
                    " произвел: " + value + " (размер буфера: " + buffer.size() + ")");
                
                // Уведомляем потребителя, что появились данные
                buffer.notifyAll();
            }
        }
    }
    
    static class Consumer implements Runnable {
        private final Random random = new Random();
        
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    consume();
                    Thread.sleep(random.nextInt(800)); // Имитация работы
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void consume() throws InterruptedException {
            synchronized (buffer) {
                // Ждем, пока в буфере появятся данные
                while (buffer.isEmpty()) {
                    System.out.println(Thread.currentThread().getName() + ": Буфер пуст, жду...");
                    buffer.wait();
                }
                
                int value = buffer.poll();
                System.out.println(Thread.currentThread().getName() + 
                    " потребил: " + value + " (размер буфера: " + buffer.size() + ")");
                
                // Уведомляем производителя, что освободилось место
                buffer.notifyAll();
            }
        }
    }
}