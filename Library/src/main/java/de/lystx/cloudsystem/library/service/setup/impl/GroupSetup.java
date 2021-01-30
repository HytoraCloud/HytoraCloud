package de.lystx.cloudsystem.library.service.setup.impl;


import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class GroupSetup extends Setup {

    @SetupPart(id = 1, question = "How should this group be named?")
    private String serverName;

    @SetupPart(id = 2, question = "How much RAM does this group may use as maximum (in MB) ?", forbiddenAnswers = {"0"})
    private int maxMem;

    @SetupPart(id = 3, question = "How much RAM does this group have to use (in MB) ?", forbiddenAnswers = {"0"})
    private int minMem;

    @SetupPart(id = 4, question = "Should this group be dynamic? true | false", onlyAnswers = {"true", "false"})
    private boolean dynamic;

    @SetupPart(id = 5, question = "How many servers should always be online?", forbiddenAnswers = {"0"})
    private int minServer;

    @SetupPart(id = 6, question = "How many servers should maximum be online? (-1 for unlimited)", forbiddenAnswers = {"0"})
    private int maxyServer;

    @SetupPart(id = 7, question = "How many players must be online to start a new service (in percent)")
    private int newPlayersInPercent;

    @SetupPart(id = 8, question = "What type is this group (SPIGOT / PROXY)", onlyAnswers = {"spigot", "proxy"}, exitAfterAnswer = {"proxy"})
    private String type;

    @SetupPart(id = 9, question = "Is this is a lobbyServer?", onlyAnswers = {"true", "false"})
    private boolean lobby;

    @SetupPart(id = 10, question = "How many players may be online at maximum ?", forbiddenAnswers = {"0"})
    private int maxPlayers;


}
