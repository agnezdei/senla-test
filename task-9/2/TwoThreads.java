public class TwoThreads {
    private static final Object lock = new Object();
    private static int turn = 1; // 1 - первый поток, 2 - второй
    
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> printName("Thread-1", 1));
        Thread t2 = new Thread(() -> printName("Thread-2", 2));
        
        t1.start();
        t2.start();
    }
    
    private static void printName(String name, int threadNumber) {
        for (int i = 0; i < 5; i++) {
            synchronized (lock) {
                // Ждем своей очереди
                while (turn != threadNumber) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                
                System.out.println(name);
                
                // Передаем очередь другому потоку
                turn = (threadNumber == 1) ? 2 : 1;
                
                // Будим все ожидающие потоки
                lock.notifyAll();
            }
        }
    }
}