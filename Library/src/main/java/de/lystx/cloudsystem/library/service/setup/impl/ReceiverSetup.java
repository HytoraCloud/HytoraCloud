package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class ReceiverSetup extends Setup {

    @SetupPart(question = "What's the host of your CloudSystem?", id = 1, forbiddenAnswers = {""})
    private String host;

    @SetupPart(question = "What's the port of your CloudSystem?", id = 2, forbiddenAnswers = {""})
    private Integer port;

    @SetupPart(question = "What's the name of this Receiver ?", id = 3, forbiddenAnswers = {""})
    private String name;
}
