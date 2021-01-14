package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.impl.GroupSetup;

import java.util.UUID;
import java.util.function.Consumer;

public class CreateCommand extends Command {

    public CreateCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole colouredConsoleProvider, String command, String[] args) {
            CloudSystem.getInstance().getService(CommandService.class).setActive(false);
            GroupSetup groupSetup = new GroupSetup();
            groupSetup.start(CloudSystem.getInstance().getConsole(), setup -> {
                if (setup.wasCancelled()) {
                    CloudSystem.getInstance().getService(CommandService.class).setActive(true);
                    return;
                }
                ServiceGroup serviceGroup = new ServiceGroup(UUID.randomUUID(), groupSetup.getServerName(), "default", ServiceType.valueOf(groupSetup.getType()), groupSetup.getMaxyServer(),
                        groupSetup.getMinServer(), groupSetup.getMaxMem(), groupSetup.getMinMem(), groupSetup.getMaxPlayers(), groupSetup.getNewPlayersInPercent(), false, groupSetup.isLobby(), groupSetup.isDynamic());
                CloudSystem.getInstance().getService(GroupService.class).createGroup(serviceGroup);
                CloudSystem.getInstance().getService(CommandService.class).setActive(true);
                colouredConsoleProvider.getLogger().sendMessage("INFO", "§2Created ServiceGroup §a" + serviceGroup.getName() + " §7| §bMaxMB " + serviceGroup.getMaxRam() + " §7| §bMinMB " + serviceGroup.getMinRam() + " §7| §bMinServer " + serviceGroup.getMinServer() + " §7| §bMaxServer" + serviceGroup.getMaxServer());
                CloudSystem.getInstance().getService().needServices(serviceGroup);
            });
        }
    }
