package de.quantumrange.actionlib.action.impl.manager;

import de.quantumrange.actionlib.action.Action;

import java.util.LinkedList;
import java.util.Queue;

public class ActionThread extends Thread {

	private final int id;
	private boolean run, wait, debug;
	private final Queue<Action<?>> actionQueue;

	public ActionThread(int id, boolean debug) {
		this.id = id;
		this.debug = debug;
		this.run = true;
		this.wait = false;
		this.actionQueue = new LinkedList<>();
	}

	@Override
	public void run() {
		while (run) {
			synchronized (actionQueue) {
				while (wait || actionQueue.isEmpty()) {
					try {
						//noinspection BusyWait
						sleep(10);
					} catch (InterruptedException ignored) { }
//					try {
//						actionQueue.wait();
//					} catch (InterruptedException ignored) { }
				}

				if (debug) System.out.printf("[ACTION-THREAD-%d] Pull next Action. %d Actions in the queue.%n",
						id, actionQueue.size() - 1);
				Action<?> action;

				synchronized (actionQueue) {
					action = actionQueue.poll();
				}
				if (action == null) continue; // if that should ever happen

				if (debug) System.out.printf("[ACTION-THREAD-%d] Execute Action%n", id);
				action.submit(this, throwable -> { });
			}
		}
	}

	public void clearActions() {
		pauseThread();
		actionQueue.get().clear();
	}

	public void addAction(Action<?> action) {
		actionQueue.get().add(action);
	}

	public int getJobs() {
		return actionQueue.get().size();
	}

	public void pauseThread() {
		wait = true;
	}

	public void resumeThread() {
		synchronized (actionQueue) {
			wait = false;
			actionQueue.notify();
		}
	}

	public void disable() {
		run = false;
	}

	public void enable() {
		run = true;
		start();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}
}
