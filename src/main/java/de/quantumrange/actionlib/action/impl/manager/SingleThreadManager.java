package de.quantumrange.actionlib.action.impl.manager;

import de.quantumrange.actionlib.action.Action;
import de.quantumrange.actionlib.action.ActionManager;

import java.util.concurrent.atomic.AtomicReference;

public class SingleThreadManager implements ActionManager {

	private final ActionThread actionThread;

	public SingleThreadManager(boolean debug) {
		actionThread = new ActionThread(0, debug);
		actionThread.start();
	}

	public SingleThreadManager() {
		this(false);
	}

	@Override
	public void queue(Action<?> action) {
		actionThread.addAction(action);
	}

	@Override
	public <T> T completion(Action<T> action) {
		actionThread.pauseThread();
		T result = executeCompletion(action);
		actionThread.resumeThread();
		return result;
	}

	public void setDebug(boolean debug) {
		actionThread.setDebug(debug);
	}

	public boolean isDebug() {
		return actionThread.isDebug();
	}

	@Override
	public void clearWaitingActions() {
		actionThread.clearActions();
	}

	public void stop() {
		clearWaitingActions();
		actionThread.disable();
	}

}
