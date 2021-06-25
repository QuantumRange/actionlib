package de.quantumrange.actionlib.action;

import java.util.concurrent.atomic.AtomicReference;

public interface ActionManager {

	void queue(Action<?> action);
	<T> T completion(Action<T> action);
	void clearWaitingActions();

	/**
	 * Stops the current Thread until the Action is processed.
	 *
	 * @param action the Action
	 * @param <T> the Action Type
	 * @return the result of the calculation of the action.
	 */
	default <T> T executeCompletion(Action<T> action) {
		AtomicReference<Throwable> error = new AtomicReference<>(null);
		T result = action.submit(null, error::set);
		Throwable throwable = error.get();
		if (throwable != null) {
			if (throwable instanceof Error) throw (Error) throwable;
			else throwable.printStackTrace();
		};

		return result;
	}

}
