package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class WrapperSetup extends Setup {

    @SetupPart(id = 1, question = "What's the host of your Launcher?")
    private String host;

    @SetupPart(id = 2, question = "What's the port of your Launcher?")
    private int port;

    @SetupPart(id = 3, question = "What's the name of this Wrapper?")
    private String name;
}
