package de.lystx.cloudsystem.library.service.command;

public interface CloudCommandSender {

    String getName();

    void sendMessage(String message);
}
