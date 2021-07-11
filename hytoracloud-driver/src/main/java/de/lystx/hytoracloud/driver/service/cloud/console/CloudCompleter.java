package de.lystx.hytoracloud.driver.service.cloud.console;

import de.lystx.hytoracloud.driver.service.managing.command.CommandService;
import de.lystx.hytoracloud.driver.service.managing.command.command.CommandInfo;
import de.lystx.hytoracloud.driver.service.managing.command.command.TabCompletable;
import jline.console.completer.Completer;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class CloudCompleter implements Completer {

    private final CommandService commandService;

    /**
     * Console TabCompleter
     * @param buffer
     * @param cursor
     * @param candidates
     * @return
     */
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

                List<String> list = ((TabCompletable) object).onTabComplete(this.commandService.getDriver(), args);
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
