package de.quantumrange.actionlib;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

public interface ActionManager {

	/**
	 * Places an action on the queue.
	 *
	 * @param action which must be placed on the queue.
	 * @param <T> can be anything
	 */
	<T> void queue(@Nonnull Action<T> action);

	/**
	 * Calculates the result immediately.
	 *
	 * @param action  which must be calculate immediately.
	 * @param <T> can be anything
	 * @return the finish calculate result.
	 */
	default <T> T completion(@Nonnull Action<T> action) {
		AtomicReference<Throwable> error = new AtomicReference<>(null);
		long millis = LocalDateTime.now().until(action.getDeadline(), ChronoUnit.MILLIS);
		if (millis < 0) millis = 0;
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) { }
		T result = action.submit(null, error::set);
		Throwable throwable = error.get();
		if (throwable != null) {
			if (throwable instanceof Error) throw (Error) throwable;
			else throwable.printStackTrace();
		};

		return result;
	}

}
