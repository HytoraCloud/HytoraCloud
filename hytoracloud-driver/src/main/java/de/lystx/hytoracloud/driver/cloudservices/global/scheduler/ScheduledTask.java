package de.lystx.hytoracloud.driver.cloudservices.global.scheduler;

import de.lystx.hytoracloud.driver.commons.interfaces.BooleanRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Class used to run a Task
 * async or sync and for once
 * or even multiple times
 * you can cancel the Task at any time
 */
@Getter @RequiredArgsConstructor @Setter
public class ScheduledTask implements Runnable {

	//Constructor parameters
	private final boolean sync;
	private final Runnable runnable;
	private final int id;
	private final boolean repeating;

	//Non final fields
	private int runTimes;
	private boolean cancelled;
	private boolean error;

	private List<Consumer<ScheduledTask>> taskConsumers = new ArrayList<>();
	private List<BooleanRequest> cancelWhens = new ArrayList<>();

	/**
	 * Adds a consumer to this task
	 *
	 * @param consumer the consumer
	 */
	public ScheduledTask addConsumer(Consumer<ScheduledTask> consumer) {
		this.taskConsumers.add(consumer);
		return this;
	}

	/**
	 * Adds a Request when to cancel this task
	 *
	 * @param booleanRequest the request
	 */
	public ScheduledTask cancelIf(BooleanRequest booleanRequest) {
		this.cancelWhens.add(booleanRequest);
		return this;
	}

	/**
	 * Executes the current Task
	 */
	@Override
	public void run() {
		if (cancelled || error) {
			return;
		}

		for (BooleanRequest cancelWhen : this.cancelWhens) {
			if (cancelWhen.isAccepted()) {
				this.setCancelled(true);
			}
		}

		this.runTimes++;
		try {
			this.runnable.run();
			for (Consumer<ScheduledTask> taskConsumer : this.taskConsumers) {
				taskConsumer.accept(this);
			}
		} catch (Exception e) {
			this.error = true;
			e.printStackTrace();
		}
	}



}
