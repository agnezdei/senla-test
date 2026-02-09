package com.agnezdei.hotelmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import com.agnezdei.hotelmvc.annotations.ConfigProperty;

public class ConfigProcessor {
    public static void process(Object configObject) {
        Class<?> clazz = configObject.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                processField(configObject, field);
            }
        }
    }

    private static void processField(Object configObject, Field field) {
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);

        try {
            String configFileName = annotation.configFileName();

            String propertyName = annotation.propertyName();
            if (propertyName.isEmpty()) {
                propertyName = configObject.getClass().getSimpleName() + "." + field.getName();
            }

            Properties properties = loadProperties(configFileName);

            String value = properties.getProperty(propertyName);
            if (value != null) {
                Object convertedValue = convertValue(value, field.getType());

                field.setAccessible(true);
                field.set(configObject, convertedValue);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при обработке поля " + field.getName() + ": " + e.getMessage());
        }
    }

    private static Properties loadProperties(String configFileName) {
        Properties properties = new Properties();
        
        try (InputStream input = ConfigProcessor.class.getClassLoader().getResourceAsStream(configFileName)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла конфигурации: " + configFileName +
                    ". Используются значения по умолчанию.");
        }
        
        return properties;
    }

    private static Object convertValue(String value, Class<?> targetType) {

        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.parseLong(value);
        } else if (targetType.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetType, value.toUpperCase());
        }

        return value;
    }
}