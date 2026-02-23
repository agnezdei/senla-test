package com.agnezdei.hotelmvc.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.agnezdei.hotelmvc") // сканирует и core, и web
public class WebConfig implements WebMvcConfigurer {
}