package de.lystx.cloudsystem.library.service.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor @Setter
public class Task implements Runnable {

	//Constructor parameters
	private final boolean sync;
	private final Runnable runnable;
	private final int id;
	private final boolean repeating;

	//Non final fields
	private int runTimes;
	private boolean cancelled;
	private boolean error;

	/**
	 * Executes the current Task
	 */
	@Override
	public void run() {
		if (cancelled || error) {
			return;
		}
		this.runTimes++;
		try {
			this.runnable.run();
		} catch (Exception e) {
			this.error = true;
			e.printStackTrace();
		}
	}


}
