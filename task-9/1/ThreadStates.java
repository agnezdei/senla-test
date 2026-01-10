public class ThreadStates {
    public static void main(String[] args) throws Exception {
        Object lock = new Object();
        
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            
            synchronized(lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {}
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            
            synchronized(ThreadStates.class) {
                System.out.println("Завершаем работу");
            }
        });
        
        synchronized(ThreadStates.class) {
            System.out.println("NEW: " + thread.getState());
            
            thread.start();
            System.out.println("RUNNABLE: " + thread.getState());
            
            Thread.sleep(1100);
            System.out.println("WAITING: " + thread.getState());
            
            synchronized(lock) {
                lock.notify();
            }
            
            Thread.sleep(50);
            System.out.println("WAITING завершилось, сейчас TIMED_WAITING: " + thread.getState());
            
            Thread.sleep(1100);
            System.out.println("BLOCKED: " + thread.getState());
        }
        
        Thread.sleep(50);
        thread.join();
        System.out.println("TERMINATED: " + thread.getState());
    }
}