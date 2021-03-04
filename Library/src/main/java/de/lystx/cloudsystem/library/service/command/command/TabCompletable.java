package de.lystx.cloudsystem.library.service.command.command;

import de.lystx.cloudsystem.library.CloudLibrary;

import java.util.List;

public interface TabCompletable {

    /**
     * Called when tabbing in console or ingame
     * @param cloudLibrary
     * @param args
     * @return arguments
     */
    List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args);

}