package de.lystx.cloudapi.standalone.elements;

import de.lystx.cloudapi.CloudAPI;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class Execution<T> {

    private final Runnable runnable;


    public void execute() {
        runnable.run();
    }

    public void executeAsync() {
        CloudAPI.getInstance().getExecutorService().execute(this.runnable);
    }
}
