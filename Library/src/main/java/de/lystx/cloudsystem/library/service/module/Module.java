package de.lystx.cloudsystem.library.service.module;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.event.Event;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;

import java.io.File;


@Setter @Getter
public abstract class Module {

    private ModuleInfo info;
    private CloudLibrary cloudLibrary;
    private CommandService commandService;
    private EventService eventService;
    private VsonObject config;
    private File moduleDirectory;

    /**
     * Called first
     * @param cloudLibrary
     */
    public abstract void onLoadConfig(CloudLibrary cloudLibrary);

    /**
     * Called after loading config
     * @param cloudLibrary
     */
    public abstract void onEnable(CloudLibrary cloudLibrary);

    /**
     * Called on shutdown
     * @param cloudLibrary
     */
    public abstract void onDisable(CloudLibrary cloudLibrary);

    /**
     * Calls an event
     * @param event
     */
    public void callEvent(Event event) {
        this.eventService.callEvent(event);
    }

    /**
     * Called when Module is reloaded
     * @param cloudLibrary
     */
    public void onReload(CloudLibrary cloudLibrary) {}


    /**
     * Registers an event
     * @param eventClass
     */
    public void registerEvent(Object eventClass) {
        this.eventService.registerEvent(eventClass);
    }

    /**
     * Registers a command
     * @param commandClass
     */
    public void registerCommand(Object commandClass) {
        this.commandService.registerCommand(commandClass);
    }
}
