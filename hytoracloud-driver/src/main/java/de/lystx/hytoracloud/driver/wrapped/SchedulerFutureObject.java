package de.lystx.hytoracloud.driver.wrapped;

import de.lystx.hytoracloud.driver.scheduler.SchedulerFuture;
import de.lystx.hytoracloud.driver.utils.interfaces.BooleanRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter @RequiredArgsConstructor @Setter
public class SchedulerFutureObject extends WrappedObject<SchedulerFuture, SchedulerFutureObject> implements SchedulerFuture {

	private static final long serialVersionUID = 8617358757810936900L;
	//Constructor parameters
	private final boolean sync;
	private final Runnable runnable;
	private final int id;
	private final boolean repeating;

	//Non final fields
	private int runTimes;
	private boolean cancelled;
	private boolean error;

	private List<Consumer<SchedulerFuture>> taskConsumers = new ArrayList<>();
	private List<BooleanRequest> cancelWhens = new ArrayList<>();

	@Override
	public SchedulerFuture addListener(Consumer<SchedulerFuture> consumer) {
		this.taskConsumers.add(consumer);
		return this;
	}

	@Override
	public SchedulerFuture cancelIf(BooleanRequest booleanRequest) {
		this.cancelWhens.add(booleanRequest);
		return this;
	}

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
			for (Consumer<SchedulerFuture> taskConsumer : this.taskConsumers) {
				taskConsumer.accept(this);
			}
		} catch (Exception e) {
			this.error = true;
			e.printStackTrace();
		}
	}

	@Override
	Class<SchedulerFutureObject> getWrapperClass() {
		return SchedulerFutureObject.class;
	}

	@Override
	Class<SchedulerFuture> getInterface() {
		return SchedulerFuture.class;
	}
}
