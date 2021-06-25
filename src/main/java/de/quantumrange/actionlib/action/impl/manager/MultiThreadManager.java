package de.quantumrange.actionlib.action.impl.manager;

import de.quantumrange.actionlib.action.Action;
import de.quantumrange.actionlib.action.ActionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MultiThreadManager implements ActionManager {

	private final ActionThread[] activeThreads;
	private int lastID;

	public MultiThreadManager(int threads, boolean debug) {
		this.activeThreads = new ActionThread[threads];

		this.lastID = 0;

		for (int i = 0; i < threads; i++) {
			this.activeThreads[i] = new ActionThread(i, debug);
			this.activeThreads[i].start();
		}
	}

	@Override
	public void queue(Action<?> action) {
		activeThreads[lastID].addAction(action);

		lastID++;
		lastID = lastID % activeThreads.length;
	}

	@Override
	public <T> T completion(Action<T> action) {
		pause();
		T result = executeCompletion(action);
		resume();
		return result;
	}

	@Override
	public void clearWaitingActions() {
		for (ActionThread t : activeThreads) {
			t.clearActions();
		}
	}

	public void pause() {
		for (ActionThread t : activeThreads) {
			t.pauseThread();
		}
	}

	public void resume() {
		for (ActionThread t : activeThreads) {
			t.resumeThread();
		}
	}

}
