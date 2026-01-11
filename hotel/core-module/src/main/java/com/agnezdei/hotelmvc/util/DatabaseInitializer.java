package com.agnezdei.hotelmvc.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import com.agnezdei.hotelmvc.config.DatabaseConfig;

public class DatabaseInitializer {
    public static void initialize() throws Exception {
        System.out.println("Инициализация базы данных...");
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String schema = loadResourceFile("/schema.sql");
            executeScript(stmt, schema);
            
            String data = loadResourceFile("/data.sql");
            executeScript(stmt, data);
            
            System.out.println("База данных инициализирована");
        }
    }
    
    private static void executeScript(Statement stmt, String script) throws Exception {
        if (script == null || script.trim().isEmpty()) {
            return;
        }
        
        String[] commands = script.split(";");
        for (String command : commands) {
            String trimmed = command.trim();
            if (!trimmed.isEmpty()) {
                try {
                    stmt.execute(trimmed);
                } catch (Exception e) {
                    System.err.println("Ошибка выполнения SQL: " + trimmed.substring(0, Math.min(50, trimmed.length())));
                    throw e;
                }
            }
        }
    }
    
    private static String loadResourceFile(String fileName) throws Exception {
        try (InputStream input = DatabaseInitializer.class
                .getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            
            if (input == null) {
                throw new RuntimeException("Файл не найден в classpath: " + fileName);
            }
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
}