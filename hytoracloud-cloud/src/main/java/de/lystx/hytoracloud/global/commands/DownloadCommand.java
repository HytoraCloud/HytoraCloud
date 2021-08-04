package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.utils.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import de.lystx.hytoracloud.global.setups.VersionDownload;
import java.io.File;

@CommandInfo(
        name = "download",
        description = "Manages spigot versions",
        aliases = {
                "spigot",
                "bukkit",
                "install"
        }
)
public class DownloadCommand implements CommandListener {

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        new VersionDownload().start(download -> {

            if (download.isCancelled()) {
                return;
            }

            ServerEnvironment type;

            try {
                type = ServerEnvironment.valueOf(download.getType());
            } catch (IllegalArgumentException e) {
                type = null;
            }

            if (type == null) {
                sender.sendMessage("ERROR", "§cThere is no such type named §e" + download.getType() + "§c!");
                return;
            }

            File versionsFile;

            if (type == ServerEnvironment.PROXY) {
                ProxyVersion proxyVersion = ProxyVersion.valueOf(download.getProxyVersion());

                versionsFile = new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), proxyVersion.getJarName());

                Utils.download(proxyVersion.getUrl(), versionsFile, "Downloading " + proxyVersion.name());
            } else {
                SpigotVersion spigotVersion = SpigotVersion.valueOf(download.getSpigotVersion());

                versionsFile = new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), spigotVersion.getJarName());

                Utils.download(spigotVersion.getUrl(), versionsFile, "Downloading " + spigotVersion.name());
            }
        });
    }

}
