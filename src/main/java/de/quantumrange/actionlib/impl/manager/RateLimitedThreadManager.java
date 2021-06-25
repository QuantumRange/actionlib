package de.quantumrange.actionlib.impl.manager;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.impl.actions.RateLimitedAction;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RateLimitedThreadManager extends MultiThreadManager {

	private final HashMap<Integer, Long> rateLimit;
	private final HashMap<Integer, LocalDateTime> availableLimit;

	/**
	 * Creates a ThreadPool with the specified number of threads.
	 *
	 * @param threads number of threads
	 * @see java.util.concurrent.ExecutorService
	 * @see java.util.concurrent.Executors#newFixedThreadPool(int)
	 */
	public RateLimitedThreadManager(int threads) {
		super(threads);
		this.rateLimit = new HashMap<>();
		this.availableLimit = new HashMap<>();
	}

	/**
	 * Uses only a specified part of the computer threads.
	 *
	 * @see RateLimitedThreadManager#RateLimitedThreadManager(int)
	 * @param percent is between 0.0 - 1.0. Where 0.0 is 0% and 1.0 is 100%.
	 */
	public RateLimitedThreadManager(float percent) {
		super(percent);
		this.rateLimit = new HashMap<>();
		this.availableLimit = new HashMap<>();
	}

	/**
	 * Uses all threads that the processor has.
	 * @see RateLimitedThreadManager#RateLimitedThreadManager(int)
	 * @see Runtime#availableProcessors()
	 */
	public RateLimitedThreadManager() {
		super();
		this.rateLimit = new HashMap<>();
		this.availableLimit = new HashMap<>();
	}

	/**
	 * Sets a time period for the id to wait for another action of the type until the timeout is over.
	 *
	 * @param id the RateLimit ID
	 * @param timeout the RateLimit timeout after execution.
	 */
	public void registerRateLimit(int id, long timeout) {
		if (rateLimit.containsKey(id)) rateLimit.replace(id, timeout);
		else rateLimit.put(id, timeout);
	}

	public HashMap<Integer, LocalDateTime> getAvailableLimit() {
		return availableLimit;
	}

	public HashMap<Integer, Long> getRateLimit() {
		return rateLimit;
	}

	@Override
	public <T> void queue(@Nonnull Action<T> action) {
		if (action instanceof RateLimitedAction<T> rateAction) {
			super.queue(action.deadline(getTimeout(rateAction.getId())));
		} else super.queue(action);
	}

	@Override
	public <T> T completion(@Nonnull Action<T> action) {
		if (action instanceof RateLimitedAction<T> rateAction) {
			return super.completion(action.deadline(getTimeout(rateAction.getId())));
		} else return super.completion(action);
	}

	/**
	 * Takes the current RaidLimit and adds it to the current timeout.
	 * This will be saved for the next {@link RateLimitedAction}.
	 * If there is no timeout or the current timeout has expired, a new one is created.
	 *
	 * @param id the RateLimit ID
	 * @return the time until the RateLimit for this ID is off.
	 */
	public LocalDateTime getTimeout(int id) {
		if (rateLimit.containsKey(id)) {
			// If there is no timeout for the RateID yet.
			if (!availableLimit.containsKey(id)) availableLimit.put(id, LocalDateTime.now());
			// If the last Raid ID has expired.
			if (availableLimit.get(id).isBefore(LocalDateTime.now())) availableLimit.replace(id, LocalDateTime.now());
			// RateLimit timeout add to the current timeout.
			LocalDateTime time = availableLimit.get(id);
			availableLimit.replace(id, time.plus(rateLimit.get(id), ChronoUnit.MILLIS));
			return time;
		} else {
			System.err.printf("""
					The ID %s was not stored as RateLimit.
					If no RateLimit should exist for an ID.
					""", id);
			return LocalDateTime.now();
		}
	}
}
