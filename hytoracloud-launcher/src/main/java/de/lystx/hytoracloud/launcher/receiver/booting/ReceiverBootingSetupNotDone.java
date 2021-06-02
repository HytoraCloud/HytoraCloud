package de.lystx.hytoracloud.launcher.receiver.booting;

import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import de.lystx.hytoracloud.driver.enums.Spigot;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.launcher.receiver.impl.ReceiverSetup;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.service.util.utillity.Value;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ReceiverBootingSetupNotDone {

    public ReceiverBootingSetupNotDone(Receiver receiver) {

        Value<Spigot> spigot = new Value<>();
        Value<String> bungeeCord = new Value<>();

        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getParent().getConsole().getLogger().sendMessage("§b\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n");
        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§7» §aYou've chosen to use the Multi-Root feature on HytoraCloud");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§7» §aGo through the Setup and enter your infos");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§7» §aDo not forget to turn on 'useWrapper' in the config of your main CloudSystem!");
        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getInstance(CommandService.class).setActive(false);
        new ReceiverSetup().start(receiver.getParent().getConsole(), setup -> {
            if (setup.isCancelled()) {
                receiver.getParent().getConsole().getLogger().sendMessage("ERROR", "§cYou are not allowed to cancel this setup!");
                System.exit(0);
                return;
            }
            spigot.setValue(Spigot.byKey(setup.getSpigotVersion()));
            bungeeCord.setValue(setup.getBungeeCordType());
            receiver.getInstance(CommandService.class).setActive(true);

            receiver.getParent().getConsole().sendMessage("INFO", "§7Now downloading §bBungeeCord §7and §bSpigot§h...");
            Updater.download(spigot.getValue().getUrl(), new File(receiver.getInstance(FileService.class).getVersionsDirectory(), "spigot.jar"), "Downloading Spigot");
            Updater.download(bungeeCord.getValue().equalsIgnoreCase("WATERFALL") ? "https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/401/downloads/waterfall-1.16-401.jar" : "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", new File(receiver.getInstance(FileService.class).getVersionsDirectory(), "bungeeCord.jar"), "Downloading " + bungeeCord.getValue());

            receiver.getInstance(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(receiver.getInstance(FileService.class).getGlobalDirectory(), "server-icon.png"));

            Map<String, Object> map = new HashMap<>();
            map.put("proxyStartPort", 25565);
            map.put("serverStartPort", 30000);
            ReceiverInfo receiverInfo = new ReceiverInfo(setup.getName(), setup.getHost(), setup.getPort(), true, map);
            receiver.getInstance(ConfigService.class).setReceiverInfo(receiverInfo);
            receiver.getInstance(ConfigService.class).save();
            receiver.getParent().getConsole().getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The receiver will now stop and you will have to restart it...");
            System.exit(0);
        });
    }
}
