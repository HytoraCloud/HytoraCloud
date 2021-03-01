package de.lystx.cloudsystem.library.service.command.base;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;

import java.util.UUID;

public interface CloudCommandSender {

    String getName();

    UUID getUniqueId();

    boolean hasPermission(String permission);

    void kick(String reason);

    void connect(String server);

    void fallback();

    void update();

    void sendMessage(String message);

    void sendComponent(CloudComponent cloudComponent);

    void sendMessage(String prefix, String message);
}
