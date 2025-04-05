package com.kmicro.product.dtos;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Logs {


    private String name;
    private String level;

public  Logs(Logger  logger){
    this.name = logger.getName();
    this.level = logger.getEffectiveLevel().toString();
}

    @JsonCreator
    public Logs(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Logs{" +
                "name='" + name + '\'' +
                ", level='" + level + '\'' +
                '}';
    }

}
