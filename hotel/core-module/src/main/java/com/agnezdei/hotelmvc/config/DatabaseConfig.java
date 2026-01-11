package com.agnezdei.hotelmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    // Singleton instance
    private static DatabaseConfig instance;
    
    // Единственное соединение для всего приложения
    private static Connection connection;
    private final Properties properties;
    
    // Приватный конструктор
    private DatabaseConfig() {
        properties = loadProperties();
        initializeDriver();
    }
    
    // Глобальная точка доступа
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    // СТАТИЧЕСКИЙ метод для получения соединения (теперь может вызываться из static контекста)
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }
    
    // Закрытие соединения
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            } finally {
                connection = null; // Обнуляем, чтобы можно было пересоздать
            }
        }
    }
    
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                throw new RuntimeException("Файл database.properties не найден в classpath");
            }
            props.load(input);
            return props;
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации БД", e);
        }
    }
    
    private void initializeDriver() {
        try {
            Class.forName(properties.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер БД не найден: " + properties.getProperty("db.driver"), e);
        }
    }
    
    private static Connection createConnection() throws SQLException {
        DatabaseConfig instance = getInstance();
        String url = instance.properties.getProperty("db.url");
        String user = instance.properties.getProperty("db.user", "");
        String password = instance.properties.getProperty("db.password", "");
        
        Connection conn = DriverManager.getConnection(url, user, password);
        
        // Настройки для SQLite
        if (url.contains("sqlite")) {
            try (var stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA journal_mode = WAL");
                stmt.execute("PRAGMA synchronous = NORMAL");
            }
        }
        
        return conn;
    }
    
    // Геттер для свойств (если нужно)
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}