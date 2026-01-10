import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimePrintingThread extends Thread {
    private final int intervalSeconds;
    private final DateTimeFormatter formatter;
    private volatile boolean running = true;
    
    public TimePrintingThread(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
        this.formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.setDaemon(true); // Делаем поток демоном
    }
    
    @Override
    public void run() {
        System.out.println("Служебный поток запущен. Интервал: " + intervalSeconds + " сек.");
        
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                // Получаем текущее системное время
                String currentTime = LocalTime.now().format(formatter);
                System.out.println("Текущее время: " + currentTime);
                
                // Ждем указанное количество секунд
                Thread.sleep(intervalSeconds * 1000L);
            }
        } catch (InterruptedException e) {
            System.out.println("Поток прерван");
        }
        
        System.out.println("Служебный поток завершен.");
    }
    
    public void stopThread() {
        this.running = false;
        this.interrupt();
    }
    
    public static void main(String[] args) throws InterruptedException {
        // Создаем служебный поток с интервалом 2 секунды
        TimePrintingThread timeThread = new TimePrintingThread(2);
        
        System.out.println("Запускаем служебный поток...");
        timeThread.start();
        
        Thread.sleep(10000);
        
        System.out.println("Останавливаем служебный поток...");
        timeThread.stopThread();
        timeThread.join();
        
        System.out.println("Программа завершена.");
    }
}