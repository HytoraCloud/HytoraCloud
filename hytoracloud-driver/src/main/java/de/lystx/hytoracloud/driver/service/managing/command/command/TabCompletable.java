package de.lystx.hytoracloud.driver.service.managing.command.command;

import de.lystx.hytoracloud.driver.CloudDriver;

import java.util.List;

public interface TabCompletable {

    /**
     * Called when tabbing in console or ingame
     *
     * @param cloudDriver the driver
     * @param args the args that are provided
     * @return tabCompletions
     */
    List<String> onTabComplete(CloudDriver cloudDriver, String[] args);

}