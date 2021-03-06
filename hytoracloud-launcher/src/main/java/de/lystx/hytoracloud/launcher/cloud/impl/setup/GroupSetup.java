package de.lystx.hytoracloud.launcher.cloud.impl.setup;


import de.lystx.hytoracloud.driver.utils.setup.GoTo;
import de.lystx.hytoracloud.driver.utils.setup.AbstractSetup;
import de.lystx.hytoracloud.driver.utils.setup.Setup;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;

@Getter
public class GroupSetup extends AbstractSetup<GroupSetup> {

    @Setup(id = 1, question = "How should this group be named?")
    private String serverName;

    @Setup(id = 2, question = "How much Memory does this group may use as maximum (in MB) ?", forbiddenAnswers = {"0"})
    private int memory;

    @Setup(id = 3, question = "Should this group be dynamic? true | false", onlyAnswers = {"true", "false"}, changeAnswers = {"yes->true", "no->false"})
    private boolean dynamic;

    @Setup(id = 4, question = "How many servers should always be online?", forbiddenAnswers = {"0"})
    private int minServer;

    @Setup(id = 5, question = "How many servers should maximum be online? (-1 for unlimited)", forbiddenAnswers = {"0"})
    private int maxyServer;

    @Setup(id = 6, question = "How many players must be online to start a new service (in percent)")
    private int newPlayersInPercent;

    @Setup(goTo = @GoTo(id = 8, value = "PROXY", elseID = 9), id = 7, question = "What type is this group (SPIGOT / PROXY)", onlyAnswers = {"spigot", "proxy"}, changeAnswers = {"PROXY->proxy"})
    private String type;

    @Setup(id = 8, question = "Should cracked users be able to join the network? (Less safe)", exitAfterAnswer = {"true", "false"})
    private boolean onlineMode;

    @Setup(id = 9, question = "Is this is a lobbyServer?", onlyAnswers = {"true", "false"}, changeAnswers = {"yes->true", "no->false"})
    private boolean lobby;

    @Setup(id = 10, question = "How many players may be online at maximum ?", forbiddenAnswers = {"0"})
    private int maxPlayers;

    @Setup(id = 11, question = "On which Receiver should this group run ? (Use " + Utils.INTERNAL_RECEIVER + " for this CloudSystem)")
    private String receiver;


}
