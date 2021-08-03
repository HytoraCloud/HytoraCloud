package de.lystx.hytoracloud.cloud.console;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.*;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.driver.command.execution.ICommand;
import jline.console.completer.Completer;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class CommandCompleter implements Completer {

    private final ICommandManager defaultCommandManager;

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        String[] input = buffer.split(" ");

        List<String> responses = new ArrayList<>();
        List<String> commands = new LinkedList<>();
        for (ICommand cloudCommand : this.defaultCommandManager.getCommands()) {
            commands.add(cloudCommand.getName());
        }
        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {
            responses.addAll(commands);
        } else {
            CommandListener listener = this.defaultCommandManager.getListener(input[0]);
            if (listener instanceof CommandListenerTabComplete) {
                CommandListenerTabComplete tabComplete = (CommandListenerTabComplete)listener;

                String[] args = buffer.split(" ");
                String testString = args[args.length - 1];

                List<String> list = tabComplete.onTabComplete(CloudDriver.getInstance(), args);
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
