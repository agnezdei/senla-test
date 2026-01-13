package com.agnezdei.hotelmvc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.agnezdei.hotelmvc.config.DatabaseConfig;

public class DatabaseInitializer {
    
    public static void initialize(DatabaseConfig databaseConfig) throws SQLException, IOException {
        executeScript(databaseConfig, "schema.sql");
        executeScript(databaseConfig, "data.sql");
    }
    
    private static void executeScript(DatabaseConfig databaseConfig, String scriptFileName) 
            throws SQLException, IOException {
        
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            
            InputStream inputStream = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream(scriptFileName);
            
            if (inputStream == null) {
                throw new IOException("Файл скрипта не найден: " + scriptFileName);
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder script = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                script.append(line);
                if (line.trim().endsWith(";")) {
                    stmt.execute(script.toString());
                    script = new StringBuilder();
                }
            }
            
            if (script.length() > 0) {
                stmt.execute(script.toString());
            }
            
        }
    }
}