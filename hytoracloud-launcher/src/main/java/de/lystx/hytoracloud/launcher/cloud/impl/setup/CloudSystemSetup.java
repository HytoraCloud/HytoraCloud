package de.lystx.hytoracloud.launcher.cloud.impl.setup;

import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.cloudservices.global.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.cloudservices.global.setup.Setup;
import lombok.Getter;

@Getter
public class CloudSystemSetup extends SetupExecutor<CloudSystemSetup> {


    public CloudSystemSetup() {
        super();
        this.cancellable = false;
        this.printHeader = true;
    }

    @Setup(
            id = 1,
            question = "On which port should this CloudSystem run?",
            forbiddenAnswers = {
                    "0",
                    "25565"
            }
    )
    private int port;

    @Setup(
            id = 2,
            question = "How many players may be online maximum?",
            message = {
                    "SETUP%%§7This value will be transfered to §bevery Proxy§h!"
            }
    )
    private int maxPlayers;

    @Setup(
            id = 3,
            question = "What's the name of the first Admin?",
            message = {
                    "SETUP%%§cBe careful, this player is the first one to receive all rights!"
            }
    )
    private String firstAdmin;

    @Setup(
            id = 4,
            question = "Should ProxyProtocol be enabled?",
            message = {
              "SETUP%%§7ProxyProtocol it basically just allows you to shove your §bProxy §7instances beyond Usages like haproxy and still §bretain ip §7information so that it can be passed along§h!",
              "SETUP%%§7If you §cstill don't understand §7please just enter §efalse §7and skip this question§h!"
            },
            onlyAnswers = {
                    "true",
                    "false"
            },
            changeAnswers = {
                    "yes->true",
                    "no->false"
            }
    )
    private boolean proxyProtocol;

    @Setup(
            id = 5,
            question = "What Database do you want to use? ",
            enumOnly = DatabaseType.class,
            message = {
                    "SETUP%%§cIf you enter an §eOnline-Database §cyou will have to enter your Database-Data in the next step!",
                    "SETUP%%§cIf you enter §eFILES §cyou don't need to enter any data of course!",
            }
    )
    private String database;

    @Setup(
            id = 6,
            question = "What Proxy Software do you want to use",
            enumOnly = ProxyVersion.class
    )
    private String bungeeCordType;

    @Setup(
            id = 7,
            question = "What Spigot version do you prefer",
            enumOnly = SpigotVersion.class
    )
    private String spigotVersion;


}
