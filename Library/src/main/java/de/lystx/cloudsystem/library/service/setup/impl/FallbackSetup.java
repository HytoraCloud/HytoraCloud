package de.lystx.cloudsystem.library.service.setup.impl;


import de.lystx.cloudsystem.library.service.setup.Setup;
import de.lystx.cloudsystem.library.service.setup.SetupPart;

public class FallbackSetup extends Setup {
    @SetupPart(id = 1, question = "How should this fallback be called?", forbiddenAnswers = {""})
    private String fallbackname;

    @SetupPart(id = 2, question = "What's the permission to acces to this fallback? (default: %none%)", forbiddenAnswers = {"default"})
    private String permission;

    public String getPermission() {
        return permission;
    }

    public String getFallbackname() {
        return fallbackname;
    }
}
