package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class CloudSetup extends Setup {

    @SetupPart(question = "What's the ip of this CloudSystem? (Use : 127.0.0.1) ", id = 1, forbiddenAnswers = {"localhost", "0"})
    private String hostname;

    @SetupPart(question = "On which port should this CloudSystem run? (Not working yet)", id = 2, forbiddenAnswers = {"0", "25565"})
    private int port;

    @SetupPart(question = "How many players may be online maximum?", id = 3)
    private int maxPlayers;

    @SetupPart(question = "What's the name of the first Admin?", id = 4)
    private String firstAdmin;

    @SetupPart(question = "Do you want the cloud to update it's self? (AutoUpdater but not working properly at the moment)", id = 5, onlyAnswers = {"true", "false"})
    private boolean autoUpdater;

    @SetupPart(question = "Enable ProxyProtocol? Used for sending MOTD data to something like a loadbalancer (Default: false)", id = 6, onlyAnswers = {"true", "false"})
    private boolean proxyProtocol;

    @SetupPart(question = "What Datbase do you want to use? (MYSQL, MONGODB, FILES) You will have to enter your date in the next step!", id = 7, onlyAnswers = {"MONGODB", "MYSQL", "FILES"})
    private String database;

}
