package com.pinKa.cloud.common;

public class Command extends AbstractMessage {
    private String command;

    public String getCommand() {
        return command;
    }

    public Command(String command) {
        this.command = command;
    }
}
