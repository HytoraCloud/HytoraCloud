package de.lystx.cloudsystem.library.service.setup.impl;


import de.lystx.cloudsystem.library.service.setup.GoTo;
import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class GroupAbstractSetup extends AbstractSetup<GroupAbstractSetup> {

    @Setup(id = 1, question = "How should this group be named?")
    private String serverName;

    @Setup(id = 2, question = "How much RAM does this group may use as maximum (in MB) ?", forbiddenAnswers = {"0"})
    private int maxMem;

    @Setup(id = 3, question = "How much RAM does this group have to use (in MB) ?", forbiddenAnswers = {"0"})
    private int minMem;

    @Setup(id = 4, question = "Should this group be dynamic? true | false", onlyAnswers = {"true", "false"}, changeAnswers = {"yes->true", "no->false"})
    private boolean dynamic;

    @Setup(id = 5, question = "How many servers should always be online?", forbiddenAnswers = {"0"})
    private int minServer;

    @Setup(id = 6, question = "How many servers should maximum be online? (-1 for unlimited)", forbiddenAnswers = {"0"})
    private int maxyServer;

    @Setup(id = 7, question = "How many players must be online to start a new service (in percent)")
    private int newPlayersInPercent;

    @Setup(goTo = @GoTo(id = 9, value = "PROXY", elseID = 10), id = 8, question = "What type is this group (SPIGOT / PROXY)", onlyAnswers = {"spigot", "proxy"}, changeAnswers = {"PROXY->proxy"})
    private String type;

    @Setup(id = 9, question = "Should cracked users be able to join the network? (Less safe)", exitAfterAnswer = {"true", "false"})
    private boolean onlineMode;

    @Setup(id = 10, question = "Is this is a lobbyServer?", onlyAnswers = {"true", "false"}, changeAnswers = {"yes->true", "no->false"})
    private boolean lobby;

    @Setup(id = 11, question = "How many players may be online at maximum ?", forbiddenAnswers = {"0"})
    private int maxPlayers;


}
