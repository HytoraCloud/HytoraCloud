package de.lystx.cloudsystem.library.service.command;

import de.lystx.cloudsystem.library.CloudLibrary;

import java.util.List;

public interface TabCompletable {

    List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args);

}