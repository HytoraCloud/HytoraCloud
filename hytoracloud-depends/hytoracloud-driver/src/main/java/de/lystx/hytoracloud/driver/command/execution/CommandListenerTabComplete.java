package de.lystx.hytoracloud.driver.command.execution;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;

import java.util.List;

public interface CommandListenerTabComplete extends CommandListener {

    /**
     * Called when tabbing in console or ingame
     *
     * @param cloudDriver the driver
     * @param args the args that are provided
     * @return tabCompletions
     */
    List<String> onTabComplete(CloudDriver cloudDriver, String[] args);

}