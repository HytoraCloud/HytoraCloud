package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class InstanceChooser extends AbstractSetup<InstanceChooser> {


    public InstanceChooser() {
        this.cancellable = false;
    }

    @Setup(
            id = 1,
            question = "Do you want to start the Cloud or the Receiver?",
            message = {"§f[§9SETUP§f] §f[§b1§f] §eCloudSystem", "§f[§9SETUP§f] §f[§b2§f] §6Receiver"}, onlyAnswers = {"1", "2"})
    private String instance;
}
