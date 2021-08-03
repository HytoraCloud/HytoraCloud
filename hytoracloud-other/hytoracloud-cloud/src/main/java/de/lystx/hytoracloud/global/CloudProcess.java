package de.lystx.hytoracloud.global;

import de.lystx.hytoracloud.global.commands.ClearCommand;
import de.lystx.hytoracloud.global.commands.HelpCommand;
import de.lystx.hytoracloud.global.commands.ReloadCommand;
import de.lystx.hytoracloud.global.commands.ShutdownCommand;

import lombok.Setter;
import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.interfaces.DriverParent;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import de.lystx.hytoracloud.driver.utils.other.KeyAuth;
import de.lystx.hytoracloud.global.setups.InstanceChooser;
import de.lystx.hytoracloud.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.console.logger.LoggerService;
import de.lystx.hytoracloud.driver.console.logger.LogService;
import de.lystx.hytoracloud.global.webserver.WebServer;
import de.lystx.hytoracloud.receiver.Receiver;
import lombok.Getter;

import java.io.File;

@Getter
public class CloudProcess extends CloudDriver implements DriverParent {

    /**
     * The webserver
     */
    @Setter
    protected WebServer webServer;

    /**
     * The console
     */
    protected CloudConsole console;

    /**
     * The authmanager for keys
     */
    protected KeyAuth keyAuth;


    public CloudProcess(CloudType cloudType) {
        super(cloudType);

        Utils.clearConsole();
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", this);

        //Services for every instance
        CloudDriver.getInstance().getServiceRegistry().registerService(new LoggerService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new LogService());

        //The console
        this.console = new CloudConsole(this.getServiceRegistry().getInstance(LoggerService.class), this.getCommandManager(), System.getProperty("user.name"));
        this.keyAuth = new KeyAuth(new File("auth.json"));

        //CHoosing instance
        if (cloudType == CloudType.NONE) {
            this.console.getCommandManager().setActive(false);
            new InstanceChooser().start(instanceChooser -> {
                if (instanceChooser.isCancelled()) {
                    console.getLogger().sendMessage("ERROR", "§cWish you a good day anyways :(");
                    System.exit(1);
                    return;
                }
                if (instanceChooser.getInstance().equalsIgnoreCase("3")) {
                    console.getLogger().sendMessage("INFO", "§7Starting §aManager§7....");
                } else {
                    console.getLogger().sendMessage("INFO", "§7Starting §b" + (instanceChooser.getInstance().equalsIgnoreCase("2") ? "Receiver" : "CloudSystem") + " §7version §a" + CloudDriver.getInstance().getInfo().version() + " §7by §bLystx§h...");
                }
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(Utils::clearConsole, 10L);

                console.stop();
                console.interrupt();

                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
                    this.console.getCommandManager().setActive(true);
                    if (instanceChooser.getInstance().equalsIgnoreCase("2")) {
                        Receiver receiver = new Receiver();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", receiver);
                    } else {
                        CloudSystem cloudSystem = new CloudSystem();
                        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "parent", cloudSystem);
                    }
                }, 15L);
            });
        }

        //Registering commands for every instance
        this.getCommandManager().registerCommand(new ShutdownCommand(this));
        this.getCommandManager().registerCommand(new HelpCommand());
        this.getCommandManager().registerCommand(new ReloadCommand(this));
        this.getCommandManager().registerCommand(new ClearCommand(this));

    }

    public void bootstrap() {

    }

    /**
     * Shuts down the Cloud
     */
    public void shutdown() {
        super.shutdown();
        this.console.interrupt();
    }
}
