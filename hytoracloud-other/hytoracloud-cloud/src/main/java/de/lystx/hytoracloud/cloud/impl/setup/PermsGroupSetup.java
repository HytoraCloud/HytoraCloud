package de.lystx.hytoracloud.cloud.impl.setup;

import de.lystx.hytoracloud.driver.cloudservices.global.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.cloudservices.global.setup.Setup;
import lombok.Getter;

@Getter
public class PermsGroupSetup extends SetupExecutor<PermsGroupSetup> {

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

    @Setup(id = 6, question = "Whats the chatFormat of this group?", forbiddenAnswers = {""})
    private String chatFormat;

    @Setup(id = 7, question = "What groups should this group extend from? (Separate groups by \",\")")
    private String inheritances;

    @Setup(id = 8, question = "What permissions should this group have? (Separate groups by \",\")")
    private String permissions;

}
