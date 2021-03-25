package de.lystx.cloudsystem.library.service.setup.impl;

import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.setup.Setup;
import lombok.Getter;

@Getter
public class InstanceChooser extends AbstractSetup<InstanceChooser> {


    public InstanceChooser() {
        this.cancellable = false;
    }

    @Setup(id = 1, question = "Do you want to start the Cloud or the Receiver? (\"C\" = CloudSystem, \"R\" = Receiver)", onlyAnswers = {"C", "R"})
    private String instance;
}
