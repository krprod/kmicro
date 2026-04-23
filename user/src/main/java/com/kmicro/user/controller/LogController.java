package com.kmicro.user.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.kmicro.user.dtos.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user-service/logs")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class LogController {

//    @Hidden
    @PutMapping
    public ResponseEntity<Void> changeLogLevel(@RequestBody Logger logger){
        LoggerContext logcontext =(LoggerContext) LoggerFactory.getILoggerFactory();
        logcontext.getLogger(logger.getName()).setLevel(Level.valueOf(logger.getLevel()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Logger>> getLogList(){
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggers = new ArrayList<>();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            loggers.add(new Logger(logger));
        }
        return ResponseEntity.ok(loggers);
    }

}//EC
