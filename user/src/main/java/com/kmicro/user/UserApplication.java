package com.kmicro.user;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableCaching
@EnableScheduling
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
		setLogLevel(true);
	}

	private static void setLogLevel(Boolean activeProfile){
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.getLogger("ROOT").setLevel(Level.valueOf("INFO"));
//		context.getLogger("custom.logger").setLevel(Level.valueOf("ERROR"));
	}

}
