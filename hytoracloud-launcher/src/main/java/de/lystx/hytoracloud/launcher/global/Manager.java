package de.lystx.hytoracloud.launcher.global;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.NetworkService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.Utils;
import net.hytora.networking.connection.server.HytoraServer;

public class Manager extends CloudProcess {


    public Manager() {
        super(CloudType.MANAGER);

        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§f\n" +
                "    __  __      __                   ________                __\n" +
                "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                "      /____/                                                   \n" +
                "\n");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Version §7: §b" + CloudDriver.getInstance().getVersion());
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Developer §7: §bLystx");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Loading §aManager§f...");
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
        this.getParent().getConsole().getLogger().sendMessage("INFO", "§7Welcome to the §aManager§h!");
        this.getParent().getConsole().getLogger().sendMessage("INFO", "§7No §bServices §7are going to start and you can peacefully manage all of your groups or anything else!");

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", new HytoraServer(0));
    }

}
