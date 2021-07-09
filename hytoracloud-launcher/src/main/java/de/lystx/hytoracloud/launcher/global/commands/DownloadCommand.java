package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.enums.ProxyVersion;
import de.lystx.hytoracloud.driver.enums.SpigotVersion;
import de.lystx.hytoracloud.driver.service.console.CloudConsole;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.launcher.global.impl.setup.VersionDownload;
import java.io.File;

public class DownloadCommand {


    @Command(
            name = "download",
            description = "Manages spigot versions",
            aliases = {
                    "spigot",
                    "bukkit",
                    "install"
            }
    )
    public void execute(CloudCommandSender sender, String[] args) {
        new VersionDownload().start((CloudConsole)sender, download -> {

            if (download.isCancelled()) {
                return;
            }

            ServiceType type;

            try {
                type = ServiceType.valueOf(download.getType());
            } catch (IllegalArgumentException e) {
                type = null;
            }

            if (type == null) {
                sender.sendMessage("ERROR", "§cThere is no such type named §e" + download.getType() + "§c!");
                return;
            }

            CloudConsole cloudConsole = (CloudConsole)sender;
            File versionsFile;

            if (type.isProxy()) {
                ProxyVersion proxyVersion = ProxyVersion.byKey(download.getProxyVersion());

                if (proxyVersion == null) {
                    sender.sendMessage("ERROR", "§cThere is no such proxy-version named §e" + download.getProxyVersion() + "§c!");
                    return;
                }

                versionsFile = new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), proxyVersion.getJarName());

                Updater.download(proxyVersion.getUrl(), versionsFile, "Downloading " + proxyVersion.name());
            } else {
                SpigotVersion spigotVersion = SpigotVersion.byKey(download.getSpigotVersion());

                if (spigotVersion == null) {
                    sender.sendMessage("ERROR", "§cThere is no such spigot-version named §e" + download.getSpigotVersion() + "§c!");
                    return;
                }

                versionsFile = new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), spigotVersion.getJarName());

                Updater.download(spigotVersion.getUrl(), versionsFile, "Downloading " + spigotVersion.name());
            }
        });
    }

}
