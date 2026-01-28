package com.agnezdei.hotelmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static DatabaseConfig instance;
    private final Properties properties;

    private DatabaseConfig() {
        properties = loadProperties();
        initializeDriver();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return createConnection();
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

    private Connection createConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user", "");
        String password = properties.getProperty("db.password", "");

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

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}