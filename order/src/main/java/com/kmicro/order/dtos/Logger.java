package com.kmicro.order.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Logger {

    private String name;
    private String level;

    public Logger(ch.qos.logback.classic.Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }

    @JsonCreator
    public Logger(){}
}
