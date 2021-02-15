package de.lystx.cloudsystem.commands;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

import java.lang.reflect.Field;

public class EditCommand extends CloudCommand {

    public EditCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {

        if (args.length == 3) {
            String groupName = args[0];
            ServiceGroup group = cloudLibrary.getService(GroupService.class).getGroup(groupName);

            if (group == null) {
                console.getLogger().sendMessage("ERROR", "§cThe group §e" + groupName + " §cseems not to exist!");
                return;
            }
            String key = args[1];
            String value = args[2];
            ServiceGroup newGroup = new ServiceGroup(group.getUniqueId(), group.getName(), group.getTemplate(), group.getServiceType(), group.getMaxServer(), group.getMinServer(), group.getMaxRam(), group.getMinRam(), group.getMaxPlayers(), group.getNewServerPercent(), group.isMaintenance(), group.isLobby(), group.isDynamic());
            if (key.equalsIgnoreCase("maintenance") || key.equalsIgnoreCase("mc")) {
                try {
                    newGroup.setMaintenance(Boolean.parseBoolean(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <true/false>");
                }
            } else if (key.equalsIgnoreCase("lobby")) {
                try {
                    newGroup.setLobby(Boolean.parseBoolean(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <true/false>");
                }
            } else if (key.equalsIgnoreCase("dynamic")) {
                try {
                    newGroup.setDynamic(Boolean.parseBoolean(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <true/false>");
                }
            } else if (key.equalsIgnoreCase("name") || key.equalsIgnoreCase("uniqueId")) {
                console.getLogger().sendMessage("ERROR", "§cThis value can't be changed!");
                return;
            } else if (key.equalsIgnoreCase("template")) {
                newGroup.setTemplate(value);
            } else if (key.equalsIgnoreCase("maxServer")) {
                try {
                    newGroup.setMaxServer(Integer.parseInt(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("minServer")) {
                try {
                    newGroup.setMinServer(Integer.parseInt(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("maxRam")) {
                try {
                    newGroup.setMaxRam(Integer.parseInt(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("minRam")) {
                try {
                    newGroup.setMinRam(Integer.parseInt(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("maxPlayers")) {
                try {
                    newGroup.setMaxPlayers(Integer.parseInt(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("newServerPercent")) {
                try {
                    newGroup.setNewServerPercent(Integer.parseInt(value));
                } catch (Exception e) {
                    console.getLogger().sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else {
                console.getLogger().sendMessage("ERROR", "§cValid fields: §e");
                for (Field declaredField : group.getClass().getDeclaredFields()) {
                    console.getLogger().sendMessage("ERROR", " §c> §e" + declaredField.getName());
                }
                return;
            }
            cloudLibrary.getService(GroupService.class).updateGroup(group, newGroup);
            console.getLogger().sendMessage("INFO", "§7Changed value §2" + key + " §7to §a" + value + " §7for group §b" + group.getName());
        } else {
            console.getLogger().sendMessage("ERROR", "§cedit <group> <key> <value>");
            console.getLogger().sendMessage("ERROR", "§cValid fields: §e");
            for (Field declaredField : ServiceGroup.class.getDeclaredFields()) {
                console.getLogger().sendMessage("ERROR", " §c> §e" + declaredField.getName());
            }
        }
    }

}
