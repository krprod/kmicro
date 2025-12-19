package com.kmicro.product;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
		setLogLevel(true);
	}

	private static void setLogLevel(Boolean activeProfile){
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.getLogger("ROOT").setLevel(Level.valueOf("INFO"));
//		context.getLogger("custom.logger").setLevel(Level.valueOf("ERROR"));
	}
}
