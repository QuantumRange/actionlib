package de.quantumrange.actionlib.impl.manager;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.ActionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadManager implements ActionManager {

	private final ExecutorService service;

	public MultiThreadManager(int threads) {
		this.service = Executors.newFixedThreadPool(threads);
	}

	@Override
	public void queue(Action<?> action) {
		service.execute(new ActionThread(action));
	}

	@Override
	public <T> T completion(Action<T> action) {
		return executeCompletion(action);
	}

}
