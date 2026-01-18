package com.kmicro.notification.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.kmicro.notification.dtos.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notification-service/logs")
public class LogController {

//    @Hidden
    @PutMapping
    public ResponseEntity<Void> changeLogLevel(@RequestBody Logger logger){
        LoggerContext logcontext =(LoggerContext) LoggerFactory.getILoggerFactory();
        logcontext.getLogger(logger.getName()).setLevel(Level.valueOf(logger.getLevel()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Logger>> getloglist(){
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggers = new ArrayList<>();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            loggers.add(new Logger(logger));
        }
        return ResponseEntity.ok(loggers);
    }

}//EC
