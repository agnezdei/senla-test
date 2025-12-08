package com.agnezdei.hotelmvc.di;

import com.agnezdei.hotelmvc.annotations.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {
    private Map<Class<?>, Object> dependencies = new HashMap<>();
    
    public void register(Class<?> type, Object instance) {
        dependencies.put(type, instance);
    }
    
    public <T> T create(Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();

        injectDependencies(instance);
        
        return instance;
    }
    
    public void inject(Object object) throws Exception {
        injectDependencies(object);
    }
    
    private void injectDependencies(Object object) throws Exception {
        Class<?> clazz = object.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> fieldType = field.getType();

                Object dependency = dependencies.get(fieldType);
                if (dependency == null) {
                    throw new RuntimeException("Зависимость не найдена: " + fieldType.getName());
                }
                
                field.setAccessible(true);
                
                field.set(object, dependency);
            }
        }
    }
}