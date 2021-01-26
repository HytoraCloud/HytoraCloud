package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.impl.GroupSetup;
import de.lystx.cloudsystem.library.service.setup.impl.PermissionGroupSetup;

import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Consumer;

public class CreateCommand extends Command {

    public CreateCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole colouredConsoleProvider, String command, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("group")) {
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
            } else if (args[0].equalsIgnoreCase("perms")) {
                PermissionGroupSetup setup = new PermissionGroupSetup();
                cloudLibrary.getService(CommandService.class).setActive(false);
                setup.start(colouredConsoleProvider, setup1 -> {
                    PermissionGroupSetup ps = (PermissionGroupSetup) setup1;
                    cloudLibrary.getService(CommandService.class).setActive(true);
                    if (setup1.wasCancelled()) {
                        return;
                    }
                    PermissionGroup permissionGroup = new PermissionGroup(
                            ps.getGroupName(),
                            ps.getGroupId(),
                            ps.getPrefix(),
                            ps.getSuffix(),
                            ps.getDisplay(),
                            new LinkedList<>(),
                            new LinkedList<>()
                    );
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().getPermissionGroups().add(permissionGroup);
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory(), CloudSystem.getInstance().getService(DatabaseService.class).getDatabase());
                    CloudSystem.getInstance().getService(PermissionService.class).load();
                    CloudSystem.getInstance().getService(PermissionService.class).loadEntries();
                    CloudSystem.getInstance().reload();
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9The permissionGroup §a" + permissionGroup.getName() + " §7| §bID " + permissionGroup.getId() + " §9was created!");
                });
            } else {
                this.correctSyntax(colouredConsoleProvider);
            }
        } else {
            this.correctSyntax(colouredConsoleProvider);
        }
        }


    @Override
    public void correctSyntax(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9create <group> §7| §bCreates a ServiceGroup");
        console.getLogger().sendMessage("INFO", "§9create <perms> §7| §bCreates a PermissionGroup");
    }
}
