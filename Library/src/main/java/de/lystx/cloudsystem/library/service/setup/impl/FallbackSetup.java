package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class FallbackSetup extends AbstractSetup<FallbackSetup> {

    @Setup(id = 1, question = "For which group is this fallback?")
    private String name;

    @Setup(id = 2, question = "What's the priority of this fallback?")
    private int id;

    @Setup(id = 3, question = "Do you need a permission for this fallback?", onlyAnswers = {"true", "false"}, exitAfterAnswer = {"false"}, changeAnswers = {"yes->true", "no->false"})
    private boolean permissionNeeded;

    @Setup(id = 4, question = "What's the permission for this fallback?")
    private String permission;
}
