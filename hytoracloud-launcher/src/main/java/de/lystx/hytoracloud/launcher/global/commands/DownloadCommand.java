package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.Console;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.launcher.global.setups.VersionDownload;
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
    public void execute(CommandExecutor sender, String[] args) {
        new VersionDownload().start(download -> {

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

            Console cloudConsole = (Console)sender;
            File versionsFile;

            if (type.isProxy()) {
                ProxyVersion proxyVersion = ProxyVersion.byKey(download.getProxyVersion());

                if (proxyVersion == null) {
                    sender.sendMessage("ERROR", "§cThere is no such proxy-version named §e" + download.getProxyVersion() + "§c!");
                    return;
                }

                versionsFile = new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), proxyVersion.getJarName());

                Utils.download(proxyVersion.getUrl(), versionsFile, "Downloading " + proxyVersion.name());
            } else {
                SpigotVersion spigotVersion = SpigotVersion.byKey(download.getSpigotVersion());

                if (spigotVersion == null) {
                    sender.sendMessage("ERROR", "§cThere is no such spigot-version named §e" + download.getSpigotVersion() + "§c!");
                    return;
                }

                versionsFile = new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), spigotVersion.getJarName());

                Utils.download(spigotVersion.getUrl(), versionsFile, "Downloading " + spigotVersion.name());
            }
        });
    }

}
