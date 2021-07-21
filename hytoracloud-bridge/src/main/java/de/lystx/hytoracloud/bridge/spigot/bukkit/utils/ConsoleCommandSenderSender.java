package de.lystx.hytoracloud.bridge.spigot.bukkit.utils;

import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.ConsoleSender;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.CloudComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@AllArgsConstructor @Getter
public class ConsoleCommandSenderSender implements ConsoleSender {

    private final CommandSender sender;

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(Object message) {
        sender.sendMessage(message.toString());
    }

    @Override
    public void sendComponent(CloudComponent cloudComponent) {

    }

    @Override
    public void sendMessage(String prefix, String message) {
        sender.sendMessage(prefix + ": " + message);
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    @Override
    public void setUniqueId(UUID uniqueId) {

    }

    @Override
    public void setName(String name) {

    }
}
