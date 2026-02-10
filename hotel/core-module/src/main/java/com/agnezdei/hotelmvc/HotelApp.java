package com.agnezdei.hotelmvc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.agnezdei.hotelmvc.config.SpringConfig;
import com.agnezdei.hotelmvc.ui.ConsoleUI;

public class HotelApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        
        ConsoleUI ui = context.getBean(ConsoleUI.class);
        
        ui.start();
    }
}