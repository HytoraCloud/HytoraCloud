package de.lystx.hytoracloud.receiver.impl.setup;

import de.lystx.hytoracloud.driver.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.setup.Setup;
import lombok.Getter;

@Getter
public class ReceiverSetup extends SetupExecutor<ReceiverSetup> {

    public ReceiverSetup() {
        this.cancellable = false;
    }

    @Setup(question = "What's the host of your Main-CloudInstance?", id = 1, forbiddenAnswers = {""})
    private String host;

    @Setup(question = "What port is your Main-CloudInstance running on?", id = 2, forbiddenAnswers = {""})
    private Integer port;

    @Setup(question = "What's the name of this Receiver ? (e.g. \"Receiver-1\")", id = 3, forbiddenAnswers = {""})
    private String name;

    @Setup(question = "How much Memory does this Receiver may use? (In MB)", id = 4, forbiddenAnswers = {""})
    private long memory;

}
