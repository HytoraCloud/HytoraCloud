package de.lystx.cloudsystem.library.service.module;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.event.raw.Event;
import lombok.Getter;
import lombok.Setter;

import java.io.File;


@Setter @Getter
public abstract class Module {

    private ModuleInfo info;
    private CloudLibrary cloudLibrary;
    private CommandService commandService;
    private EventService eventService;
    private Document config;
    private File moduleDirectory;

    public abstract void onEnable(CloudLibrary cloudLibrary);

    public abstract void onDisable(CloudLibrary cloudLibrary);

    public abstract void onLoadConfig(CloudLibrary cloudLibrary);

    public void registerCommand(Command command) {
        this.commandService.registerCommand(command);
    }

    public void callEvent(Event event) {
        this.eventService.callEvent(event);
    }

    public void registerEvent(Object eventClass) {
        this.eventService.registerEvent(eventClass);
    }
}
