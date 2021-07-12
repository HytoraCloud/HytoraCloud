package de.lystx.hytoracloud.launcher.cloud.commands;


import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceGroupObject;

import java.lang.reflect.Field;

public class EditCommand  {

    @Command(name = "edit",description = "Edits a serverGroup")
    public void execute(CloudCommandSender sender, String[] args) {

        if (args.length == 3) {
            String groupName = args[0];
            IServiceGroup group = CloudSystem.getInstance().getInstance(GroupService.class).getGroup(groupName);

            if (group == null) {
                sender.sendMessage("ERROR", "§cThe group §e" + groupName + " §cseems not to exist!");
                return;
            }
            String key = args[1];
            String value = args[2];
            IServiceGroup newGroup = new ServiceGroupObject(group.getUniqueId(), group.getName(), group.getTemplate(), group.getType(), group.getReceiver(), group.getMaxServer(), group.getMinServer(), group.getMemory(), group.getMaxPlayers(), group.getNewServerPercent(), group.isMaintenance(), group.isLobby(), group.isDynamic(), new PropertyObject());
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
                    newGroup.setMemory(Integer.parseInt(value));
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
            CloudSystem.getInstance().getInstance(GroupService.class).updateGroup(newGroup);
            sender.sendMessage("INFO", "§7Changed value §2" + key + " §7to §a" + value + " §7for group §b" + group.getName());
        } else {
            sender.sendMessage("ERROR", "§cedit <group> <key> <value>");
            sender.sendMessage("ERROR", "§cValid fields: §e");
            for (Field declaredField : IServiceGroup.class.getDeclaredFields()) {
                sender.sendMessage("ERROR", " §c> §e" + declaredField.getName());
            }
        }
    }

}
