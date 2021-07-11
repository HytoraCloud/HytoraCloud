package de.lystx.hytoracloud.launcher.cloud.impl.setup;

import de.lystx.hytoracloud.driver.utils.setup.AbstractSetup;
import de.lystx.hytoracloud.driver.utils.setup.Setup;
import lombok.Getter;

@Getter
public class CloudSetup extends AbstractSetup<CloudSetup> {


    public CloudSetup() {
        this.cancellable = false;
        this.printHeader = false;
        this.customHeader = "\n\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n\n";
    }

    @Setup(
            id = 1,
            question = "What's the ip of this CloudSystem? ",
            message = {
                    "INFO%% §cIf you do not use §eMultiRoot §cthen just type §elocalhost§c!",
            },
            changeAnswers = {
                    "localhost->0",
                    "127.0.0.1->0"
            }
    )
    private String hostname;

    @Setup(
            id = 2,
            question = "On which port should this CloudSystem run?",
            forbiddenAnswers = {
                    "0",
                    "25565"
            }
    )
    private int port;

    @Setup(
            id = 3,
            question = "How many players may be online maximum?"
    )
    private int maxPlayers;

    @Setup(
            id = 4,
            question = "What's the name of the first Admin?",
            message = {
                    "INFO%% §cBe careful!",
                    "INFO%% §cThis player is the first one to receive all rights!"
            }
    )
    private String firstAdmin;

    @Setup(
            id = 5,
            question = "Do you want the cloud to update it's self?",
            message = {
                    "INFO%% §cSadly the AutoUpdater is not supported at the moment!",
                    "INFO%% §cJust enter §efalse §cor else there will be errors!"
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
    private boolean autoUpdater;

    @Setup(
            id = 6,
            question = "Enable ProxyProtocol? If you don't know what this is just type false!",
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
            id = 7,
            question = "What Datbase do you want to use? (MYSQL, MONGODB, FILES)",
            message = {
                    "INFO%% §cIf you enter an §eOnline-Database §clike MongoDB or MySQL you will have to enter your data in the next step!",
                    "INFO%% §cIf you enter §eFiles §cyou don't need to enter any data of course!",
            },
            onlyAnswers = {
                    "MONGODB",
                    "MYSQL",
                    "FILES"
            }
    )
    private String database;

    @Setup(
            id = 8,
            question = "What Proxy Software do you want to use ? (WATERFALL, BUNGEECORD, VELOCITY)",
            onlyAnswers = {
                    "BUNGEECORD",
                    "WATERFALL",
                    "VELOCITY"
            }
    )
    private String bungeeCordType;

    @Setup(
            id = 9,
            question = "What Spigot version do you prefer ? (1.8.8, 1.9, 1.10, 1.11 etc...)",
            message = {
                    "INFO%% §cI don't know why but after entering the version you have to hit §eenter §cagain to confirm it!",
                    "INFO%% §cIm still trying to figure out the error!",
            }
    )
    private String spigotVersion;


}
