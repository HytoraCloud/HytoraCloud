package de.lystx.hytoracloud.launcher.receiver.booting;

import de.lystx.hytoracloud.driver.cloudservices.other.Updater;
import de.lystx.hytoracloud.driver.utils.utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import lombok.Getter;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.elements.other.HytoraLogin;

@Getter
public class ReceiverBootingSetupDone {

    private int tries = 0;
    private final Receiver receiver;

    public ReceiverBootingSetupDone(Receiver receiver) {
        this.receiver = receiver;
        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getParent().getConsole().getLogger().sendMessage("§b\n" +
                "    __  __      __                   ________                __\n" +
                "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                "      /____/                                                   \n" +
                "\n");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§9Version §7: §b" + Updater.getCloudVersion());
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§9Developer §7: §bLystx");
        receiver.getParent().getConsole().getLogger().sendMessage("INFO", "§bLoading §fReceiver§9...");
        receiver.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§7Trying to connect to §bCloudSystem§h...");
        this.connect();
    }


    public void connect() {
        tries++;
        if (tries > 10) {
            receiver.getParent().getConsole().getLogger().sendMessage("ERROR", "§cTried §e10x §cto connect to CloudSystem! Shutting down...");
            receiver.shutdown();
            return;
        }
        ReceiverInfo info = receiver.getInstance(ConfigService.class).getReceiverInfo();
        new Thread(() -> {
            try {


                HytoraClient hytoraClient = new HytoraClient(info.getIpAddress(), info.getPort());

                hytoraClient.login(new HytoraLogin(info.getName())).createConnection();

                receiver.setCloudClient(hytoraClient);

            } catch (Exception e) {
                receiver.getParent().getConsole().getLogger().sendMessage("ERROR", "§cNo connection to CloudSystem §ccould be build up! §h[§b" + info.getIpAddress() + "§7:" + "§b" + info.getPort() + "§7/§bTried: " + tries + "x§h]");
                this.connect();
            }
        }, "hytoraCloud_Receiver").start();
    }
}
