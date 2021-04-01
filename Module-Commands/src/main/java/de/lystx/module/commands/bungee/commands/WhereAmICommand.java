package de.lystx.module.commands.bungee.commands;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class WhereAmICommand {


    @Command(name = "whereami")
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            Service s = CloudAPI.getInstance().getNetwork().getService(player.getService().getName());
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§6" + s.getName() + "#" + s.getUniqueId() + " §8«» §a" + CloudAPI.getInstance().getService().getName());

        }
    }
}
