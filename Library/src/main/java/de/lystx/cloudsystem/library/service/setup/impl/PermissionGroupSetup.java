package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class PermissionGroupSetup extends AbstractSetup<PermissionGroupSetup> {

    @Setup(id = 1, question = "How should this group be called?", forbiddenAnswers = {""})
    private String groupName;

    @Setup(id = 2, question = "Whats the id of this group?", forbiddenAnswers = {"0", "-1"})
    private Integer groupId;

    @Setup(id = 3, question = "Whats the prefix of this group?", forbiddenAnswers = {""})
    private String prefix;

    @Setup(id = 4, question = "Whats the suffix of this group?", forbiddenAnswers = {""})
    private String suffix;

    @Setup(id = 5, question = "Whats the display of this group?", forbiddenAnswers = {""})
    private String display;

}
