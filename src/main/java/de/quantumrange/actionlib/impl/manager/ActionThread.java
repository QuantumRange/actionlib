package de.quantumrange.actionlib.impl.manager;

import de.quantumrange.actionlib.Action;

public record ActionThread(Action<?> action) implements Runnable {

	@Override
	public void run() {
		if (action.getCheck() == null || action.getCheck().getAsBoolean()) {
			action.submit(this, throwable -> { });
		}
	}

}
