package de.lystx.cloudsystem.receiver.booting;

import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.enums.Spigot;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.setup.impl.ReceiverSetup;
import de.lystx.cloudsystem.library.service.util.Value;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.Getter;

import java.io.File;

@Getter
public class ReceiverBootingSetupNotDone {

    public ReceiverBootingSetupNotDone(Receiver receiver) {

        Value<Spigot> spigot = new Value<>();
        Value<String> bungeeCord = new Value<>();

        receiver.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getConsole().getLogger().sendMessage("§b\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n");
        receiver.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getConsole().getLogger().sendMessage("KNOWN-BUG", "§7» §cSetup crashes if trying to use history (arrow keys)");
        receiver.getConsole().getLogger().sendMessage("KNOWN-BUG", "§7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
        receiver.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getService(CommandService.class).setActive(false);
        new ReceiverSetup().start(receiver.getConsole(), setup -> {
            receiver.getService(CommandService.class).setActive(true);
            if (setup.isCancelled()) {
                receiver.getConsole().getLogger().sendMessage("ERROR", "§cYou are not allowed to cancel this setup!");
                System.exit(0);
                return;
            }

            receiver.getConsole().sendMessage("INFO", "§7Now downloading §bBungeeCord §7and §bSpigot§h...");
            receiver.getService(FileService.class).download(spigot.getValue().getUrl(), new File(receiver.getService(FileService.class).getVersionsDirectory(), "spigot.jar"));
            receiver.getService(FileService.class).download(bungeeCord.getValue().equalsIgnoreCase("WATERFALL") ? "https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/401/downloads/waterfall-1.16-401.jar" : "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", new File(receiver.getService(FileService.class).getVersionsDirectory(), "bungeeCord.jar"));

            ReceiverInfo receiverInfo = new ReceiverInfo(setup.getName(), setup.getHost(), setup.getPort(), true);
            receiver.getService(ConfigService.class).setReceiverInfo(receiverInfo);
            receiver.getService(ConfigService.class).save();
            receiver.getConsole().getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The receiver will now stop and you will have to restart it...");
            System.exit(0);
        });
    }
}
