package de.lystx.hytoracloud.launcher.global.impl.setup;

import de.lystx.hytoracloud.driver.utils.setup.AbstractSetup;
import de.lystx.hytoracloud.driver.utils.setup.Setup;
import lombok.Getter;

@Getter
public class InstanceChooser extends AbstractSetup<InstanceChooser> {


    public InstanceChooser() {
        this.cancellable = false;
        this.printHeader = false;
        this.customHeader = "\n" +
                "  _    _       _                   _____ _                 _ \n" +
                " | |  | |     | |                 / ____| |               | |\n" +
                " | |__| |_   _| |_ ___  _ __ __ _| |    | | ___  _   _  __| |\n" +
                " |  __  | | | | __/ _ \\| '__/ _` | |    | |/ _ \\| | | |/ _` |\n" +
                " | |  | | |_| | || (_) | | | (_| | |____| | (_) | |_| | (_| |\n" +
                " |_|  |_|\\__, |\\__\\___/|_|  \\__,_|\\_____|_|\\___/ \\__,_|\\__,_|\n" +
                "          __/ |                                              \n" +
                "         |___/                                               ";
    }

    @Setup(
            id = 1,
            question = "Do you want to start the Cloud or the Receiver?",
            message = {
                    "Setup%%§f[§b1§f] §eCloudSystem",
                    "Setup%%§f[§b2§f] §6Receiver"
            },
            onlyAnswers = {
                    "1",
                    "2"
            }
            )
    private String instance;
}
