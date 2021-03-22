package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class ReceiverSetup extends AbstractSetup<ReceiverSetup> {

    @Setup(question = "What's the host of your CloudSystem?", id = 1, forbiddenAnswers = {""})
    private String host;

    @Setup(question = "What's the port of your CloudSystem?", id = 2, forbiddenAnswers = {""})
    private Integer port;

    @Setup(question = "What's the name of this Receiver ?", id = 3, forbiddenAnswers = {""})
    private String name;

    @Setup(question = "What Proxy Software do you prefer ? (WATERFALL, BUNGEECORD)", id = 4, onlyAnswers = {"BUNGEECORD", "WATERFALL"})
    private String bungeeCordType;

    @Setup(question = "What Spigot version do you prefer ? (1.8.8, 1.9, 1.10, 1.11 etc...)", id = 5)
    private String spigotVersion;
}
