package de.lystx.cloudsystem.library.service.console;


import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.command.command.CommandInfo;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.console.logger.LoggerService;
import jline.console.completer.Completer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

@Getter
public class CloudConsole extends Thread implements CloudCommandSender {

    private final LoggerService logger;
    private final CommandService commandManager;
    private final String buffer;

    public CloudConsole(LoggerService logger, CommandService commandManager, String buffer) {
        this.logger = logger;
        this.buffer = buffer;
        this.commandManager = commandManager;
        this.logger.getConsoleReader().addCompleter(new CloudCompleter(commandManager));
        this.start();
    }



    public void run() {
        while (!this.isInterrupted()) {
            try {
                String s = ConsoleColor.formatColorString(this.getPrefix());
                String line;
                if ((line = this.logger.getConsoleReader().readLine(s)) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    this.logger.getConsoleReader().setPrompt("");
                    this.commandManager.execute(line, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void clearScreen() {
        for (int i = 0; i < 100; i++) {
            System.out.println(" ");
        }
    }

    public String getPrefix() {
        return "§9Cloud§b@§7" + this.buffer.replace('-', ' ') + " §f» §7 ";
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void kick(String reason) {
        throw new UnsupportedOperationException("Console doesn't support : kick" );
    }

    @Override
    public void connect(String server) {
        throw new UnsupportedOperationException("Console doesn't support : connect" );
    }

    @Override
    public void fallback() {
        throw new UnsupportedOperationException("Console doesn't support : fallback" );
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Console doesn't support : update" );
    }

    @Override
    public void sendMessage(String message) {
        this.logger.sendMessage(message);
    }

    @Override
    public void sendComponent(CloudComponent cloudComponent) {
        throw new UnsupportedOperationException("Console doesn't support : sendComponent" );
    }

    @Override
    public void sendMessage(String prefix, String message) {
        this.logger.sendMessage(prefix, message);
    }



    @AllArgsConstructor
    public static class CloudCompleter implements Completer {

        private final CommandService commandService;

        @Override
        public int complete(String buffer, int cursor, List<CharSequence> candidates) {
            String[] input = buffer.split(" ");

            List<String> responses = new ArrayList<>();
            List<String> commands = new LinkedList<>();
            for (CommandInfo commandInfo : this.commandService.getCommandInfos()) {
                commands.add(commandInfo.getName());
            }
            if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {
                responses.addAll(commands);
            } else {
                Object object = this.commandService.getInvokers().get(input[0]);

                if (object instanceof TabCompletable) {
                    String[] args = buffer.split(" ");
                    String testString = args[args.length - 1];

                    List<String> list = ((TabCompletable) object).onTabComplete(this.commandService.getCloudLibrary(), args);
                    List<String> retu = new LinkedList<>();
                    for (String s : list) {
                        if (s != null && (testString.isEmpty() || s.toLowerCase().contains(testString.toLowerCase()))) {
                            retu.add(s);
                        }
                    }

                    responses.addAll(retu);
                }
            }

            Collections.sort(responses);

            candidates.addAll(responses);
            int lastSpace = buffer.lastIndexOf(' ');

            return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
        }
    }

}
