package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

@Getter
public class WhereAmICommand extends Command {


    public WhereAmICommand() {
        super("whereami");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            Service s = CloudAPI.getInstance().getService();
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§6" + s.getName() + "#" + s.getUniqueId());

        }
    }
}
