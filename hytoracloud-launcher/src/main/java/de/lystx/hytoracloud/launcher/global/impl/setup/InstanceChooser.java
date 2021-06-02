package de.lystx.hytoracloud.launcher.global.impl.setup;

import de.lystx.hytoracloud.driver.service.setup.AbstractSetup;
import de.lystx.hytoracloud.driver.service.setup.Setup;
import lombok.Getter;

@Getter
public class InstanceChooser extends AbstractSetup<InstanceChooser> {


    public InstanceChooser() {
        this.cancellable = false;
        this.printHeader = false;
    }

    @Setup(
            id = 1,
            question = "Do you want to start the Cloud or the Receiver?",
            message = {"§f[§9SETUP§f] §f[§b1§f] §eCloudSystem", "§f[§9SETUP§f] §f[§b2§f] §6Receiver"}, onlyAnswers = {"1", "2"})
    private String instance;
}
