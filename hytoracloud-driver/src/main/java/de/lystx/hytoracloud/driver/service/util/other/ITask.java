package de.lystx.hytoracloud.driver.service.util.other;

import de.lystx.hytoracloud.driver.service.util.Utils;

import java.util.function.Consumer;

public interface ITask<T> {

    /**
     * Starts a new task with all parameters
     *
     * @param object the object for the task
     * @param consumer the consumer to accept
     * @return created ITask
     */
    static <Z> ITask<Z> newTask(Z object, Consumer<ITask<Z>> consumer) {
        return new Utils.DefaultTask<>(object, consumer);
    }

    /**
     * Calls an error to handle the listeners
     *
     * @param throwable the error that happeneed
     */
    void error(Throwable throwable);

    /**
     * Calls when something is null!
     *
     * @param nulledClass the class of the nulled Object
     */
    void nulled(Class<?> nulledClass);

    /**
     * Calls when everything is successfully!
     *
     * @param object the object to accept
     */
    void success(T object);

    /**
     * Adds a Listener to this task
     *
     * @param taskListener the listener to add
     * @return current task
     */
    ITask<T> addListener(ITaskListener<T> taskListener);

    /**
     * Removes a Listener from this task
     *
     * @param taskListener the listener to remove
     * @return current task
     */
    ITask<T> removeListener(ITaskListener<T> taskListener);

    /**
     * Clears all Listeners
     * @return current Task
     */
    ITask<T> clearListeners();

    /**
     * Executes this task
     */
    void runTask();

    /**
     * Gets the object
     *
     * @return object
     */
    T get();

}
