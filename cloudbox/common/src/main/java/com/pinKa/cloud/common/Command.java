package com.pinKa.cloud.common;

public class Command extends AbstractMessage {
    private String command;
    private String name;

    public String getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public Command(String command,String name) {
        this.command = command;
        this.name=name;
    }
}
