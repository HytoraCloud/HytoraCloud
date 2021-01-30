package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class FallbackSetup extends Setup {

    @SetupPart(id = 1, question = "For which group is this fallback?")
    private String name;

    @SetupPart(id = 2, question = "What's the priority of this fallback?")
    private int id;

    @SetupPart(id = 3, question = "Do you need a permission for this fallback?", onlyAnswers = {"true", "false"}, exitAfterAnswer = {"false"})
    private boolean permissionNeeded;

    @SetupPart(id = 4, question = "What's the permission for this fallback?")
    private String permission;
}
