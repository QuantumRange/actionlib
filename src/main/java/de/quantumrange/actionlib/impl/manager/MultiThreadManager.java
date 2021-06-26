package de.quantumrange.actionlib.impl.manager;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.ActionManager;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MultiThreadManager implements ActionManager {

	private final ExecutorService service;
	private final AtomicReference<List<Action<?>>> waitingList;
	private final MultiThreadManagerThread managerThread;

	/**
	 * Creates a ThreadPool with the specified number of threads.
	 *
	 * @param threads number of threads (As soon as the result is zero or below, 1 is used.)
	 * @see ExecutorService
	 * @see Executors#newFixedThreadPool(int)
	 */
	public MultiThreadManager(int threads) {
		threads = Math.max(threads, 1);
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (threads > availableProcessors) {
			System.err.printf("""
					The number of threads (%d) is above the number of available threads (%d).
					Once the number goes above that limit of the available threads, it can no longer go faster.
					In the worst case even slower.
					""", threads, availableProcessors);
		}
		this.service = Executors.newFixedThreadPool(threads);
		this.waitingList = new AtomicReference<>(new ArrayList<>());
		this.managerThread = new MultiThreadManagerThread();
		new Thread(this.managerThread).start();
	}

	/**
	 * Uses only a specified part of the computer threads.
	 * As soon as the result is zero or below, 1 is used.
	 *
	 * @see MultiThreadManager#MultiThreadManager(int)
	 * @param percent is between 0.0 - 1.0. Where 0.0 is 0% and 1.0 is 100%.
	 */
	public MultiThreadManager(float percent) {
		this((int) (Runtime.getRuntime().availableProcessors() * percent));
	}

	/**
	 * Uses all threads that the processor has.
	 * @see MultiThreadManager#MultiThreadManager(int)
	 * @see Runtime#availableProcessors()
	 */
	public MultiThreadManager() {
		this(Runtime.getRuntime().availableProcessors());
	}

	@Override
	public <T> void queue(@Nonnull Action<T> action) {
		if (action.getDeadline().isBefore(LocalDateTime.now())) service.execute(new ActionThread(action));
		else {
			if (!this.managerThread.isRunning()) new Thread(this.managerThread).start();
			waitingList.get().add(action);
		}
	}

	private class MultiThreadManagerThread implements Runnable {

		private boolean running = true;

		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException ignored) { }

				List<Action<?>> list = waitingList.get();
				List<Integer> indexToRemove = new ArrayList<>();

				LocalDateTime now = LocalDateTime.now();

				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getDeadline().isBefore(now)) {
						indexToRemove.add(i);
					}
				}

				indexToRemove.forEach(i -> service.execute(new ActionThread(list.get(i))));
				indexToRemove.forEach(i -> waitingList.get().remove((int) i));

				if (waitingList.get().isEmpty()) running = false;
			}
		}

		public boolean isRunning() {
			return running;
		}
	}

}
