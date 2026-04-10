package com.kmicro.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
//		setLogLevel(true);
	}

//	private static void setLogLevel(Boolean activeProfile){
//		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//		context.getLogger("ROOT").setLevel(Level.valueOf("INFO"));
////		context.getLogger("custom.logger").setLevel(Level.valueOf("ERROR"));
//	}
}
