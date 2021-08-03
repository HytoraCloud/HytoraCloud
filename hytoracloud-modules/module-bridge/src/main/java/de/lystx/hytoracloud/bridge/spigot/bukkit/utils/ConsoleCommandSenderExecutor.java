package de.lystx.hytoracloud.bridge.spigot.bukkit.utils;

import de.lystx.hytoracloud.driver.command.executor.ConsoleExecutor;
import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.setup.SetupExecutor;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@AllArgsConstructor @Getter
public class ConsoleCommandSenderExecutor implements ConsoleExecutor {

    private static final long serialVersionUID = 1944182643634166341L;
    private final CommandSender sender;

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message.toString());
    }

    @Override
    public void sendMessage(ChatComponent chatComponent) {

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
    public void clearScreen() {

    }

    @Override
    public void setCurrentSetup(SetupExecutor<?> setup) {
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public ConsoleReader getConsoleReader() {
        return null;
    }

    @Override
    public Completer getCompleter() {
        return null;
    }
}
