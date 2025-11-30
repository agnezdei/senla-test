package com.agnezdei.hotelmvc.controller;

import com.agnezdei.hotelmvc.model.Hotel;
import java.io.*;

public class StateManager {
    private static final String STATE_FILE = "hotel_state.dat";
    
    public static void saveState(Hotel hotel) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(STATE_FILE))) {
            oos.writeObject(hotel);
            System.out.println("Состояние программы сохранено в " + STATE_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения состояния: " + e.getMessage());
        }
    }
    
    public static Hotel loadState() {
        File file = new File(STATE_FILE);
        if (!file.exists()) {
            System.out.println("Файл состояния не найден, будет создан новый отель");
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(STATE_FILE))) {
            Hotel hotel = (Hotel) ois.readObject();
            System.out.println("Состояние программы загружено из " + STATE_FILE);
            return hotel;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки состояния: " + e.getMessage());
            return null;
        }
    }
}