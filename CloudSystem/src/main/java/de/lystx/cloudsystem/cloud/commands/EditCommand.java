package de.lystx.cloudsystem.cloud.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketInUpdateServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

import java.lang.reflect.Field;

public class EditCommand  {

    @Command(name = "edit",description = "Edits a serverGroup")
    public void execute(CloudCommandSender sender, String[] args) {

        if (args.length == 3) {
            String groupName = args[0];
            ServiceGroup group = CloudSystem.getInstance().getService(GroupService.class).getGroup(groupName);

            if (group == null) {
                sender.sendMessage("ERROR", "§cThe group §e" + groupName + " §cseems not to exist!");
                return;
            }
            String key = args[1];
            String value = args[2];
            ServiceGroup newGroup = new ServiceGroup(group.getUniqueId(), group.getName(), group.getTemplate(), group.getServiceType(), group.getMaxServer(), group.getMinServer(), group.getMaxRam(), group.getMinRam(), group.getMaxPlayers(), group.getNewServerPercent(), group.isMaintenance(), group.isLobby(), group.isDynamic());
            if (key.equalsIgnoreCase("maintenance") || key.equalsIgnoreCase("mc")) {
                try {
                    newGroup.setMaintenance(Boolean.parseBoolean(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <true/false>");
                }
            } else if (key.equalsIgnoreCase("lobby")) {
                try {
                    newGroup.setLobby(Boolean.parseBoolean(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <true/false>");
                }
            } else if (key.equalsIgnoreCase("dynamic")) {
                try {
                    newGroup.setDynamic(Boolean.parseBoolean(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <true/false>");
                }
            } else if (key.equalsIgnoreCase("name") || key.equalsIgnoreCase("uniqueId")) {
                sender.sendMessage("ERROR", "§cThis value can't be changed!");
                return;
            } else if (key.equalsIgnoreCase("template")) {
                newGroup.setTemplate(value);
            } else if (key.equalsIgnoreCase("maxServer")) {
                try {
                    newGroup.setMaxServer(Integer.parseInt(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("minServer")) {
                try {
                    newGroup.setMinServer(Integer.parseInt(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("maxRam")) {
                try {
                    newGroup.setMaxRam(Integer.parseInt(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("minRam")) {
                try {
                    newGroup.setMinRam(Integer.parseInt(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("maxPlayers")) {
                try {
                    newGroup.setMaxPlayers(Integer.parseInt(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else if (key.equalsIgnoreCase("newServerPercent")) {
                try {
                    newGroup.setNewServerPercent(Integer.parseInt(value));
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cedit " + group.getName() + " " + key + " <integer>");
                }
            } else {
                sender.sendMessage("ERROR", "§cValid fields: §e");
                for (Field declaredField : group.getClass().getDeclaredFields()) {
                    sender.sendMessage("ERROR", " §c> §e" + declaredField.getName());
                }
                return;
            }

            CloudSystem.getInstance().sendPacket(new PacketInUpdateServiceGroup(newGroup));
            CloudSystem.getInstance().getService(GroupService.class).updateGroup(group, newGroup);
            sender.sendMessage("INFO", "§7Changed value §2" + key + " §7to §a" + value + " §7for group §b" + group.getName());
        } else {
            sender.sendMessage("ERROR", "§cedit <group> <key> <value>");
            sender.sendMessage("ERROR", "§cValid fields: §e");
            for (Field declaredField : ServiceGroup.class.getDeclaredFields()) {
                sender.sendMessage("ERROR", " §c> §e" + declaredField.getName());
            }
        }
    }

}
