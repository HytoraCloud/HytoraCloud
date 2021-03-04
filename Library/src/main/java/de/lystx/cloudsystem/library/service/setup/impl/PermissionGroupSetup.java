package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;
import lombok.Getter;

@Getter
public class PermissionGroupSetup extends Setup<PermissionGroupSetup> {

    @SetupPart(id = 1, question = "How should this group be called?", forbiddenAnswers = {""})
    private String groupName;

    @SetupPart(id = 2, question = "Whats the id of this group?", forbiddenAnswers = {"0", "-1"})
    private Integer groupId;

    @SetupPart(id = 3, question = "Whats the prefix of this group?", forbiddenAnswers = {""})
    private String prefix;

    @SetupPart(id = 4, question = "Whats the suffix of this group?", forbiddenAnswers = {""})
    private String suffix;

    @SetupPart(id = 5, question = "Whats the display of this group?", forbiddenAnswers = {""})
    private String display;

}
