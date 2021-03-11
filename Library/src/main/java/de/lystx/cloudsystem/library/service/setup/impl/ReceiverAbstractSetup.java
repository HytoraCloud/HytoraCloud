package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class ReceiverAbstractSetup extends AbstractSetup<ReceiverAbstractSetup> {

    @Setup(question = "What's the host of your CloudSystem?", id = 1, forbiddenAnswers = {""})
    private String host;

    @Setup(question = "What's the port of your CloudSystem?", id = 2, forbiddenAnswers = {""})
    private Integer port;

    @Setup(question = "What's the name of this Receiver ?", id = 3, forbiddenAnswers = {""})
    private String name;
}
