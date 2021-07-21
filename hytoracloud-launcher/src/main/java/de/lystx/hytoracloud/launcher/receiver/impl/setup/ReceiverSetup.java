package de.lystx.hytoracloud.launcher.receiver.impl.setup;

import de.lystx.hytoracloud.driver.utils.setup.AbstractSetup;
import de.lystx.hytoracloud.driver.utils.setup.Setup;
import lombok.Getter;

@Getter
public class ReceiverSetup extends AbstractSetup<ReceiverSetup> {

    public ReceiverSetup() {
        this.cancellable = false;
        this.printHeader = false;
        this.customHeader = "\n\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n\n";
    }

    @Setup(question = "What's the host of your Main-CloudInstance?", id = 1, forbiddenAnswers = {""})
    private String host;

    @Setup(question = "What port is your Main-CloudInstance running on??", id = 2, forbiddenAnswers = {""})
    private Integer port;

    @Setup(question = "What's the name of this Receiver ? (e.g. \"Receiver-1\")", id = 3, forbiddenAnswers = {""})
    private String name;

}
