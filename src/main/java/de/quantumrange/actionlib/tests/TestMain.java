package de.quantumrange.actionlib.tests;

import de.quantumrange.actionlib.action.Action;
import de.quantumrange.actionlib.action.ActionManager;
import de.quantumrange.actionlib.action.impl.actions.NormalAction;
import de.quantumrange.actionlib.action.impl.manager.MultiThreadManager;
import de.quantumrange.actionlib.action.impl.manager.SingleThreadManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

public class TestMain {

	public static final ActionManager MANAGER = new MultiThreadManager(8, true);

	public static void main(String[] args) {

	}

	public static Action<Long> pingURL(final String url) {
		return new NormalAction<>(MANAGER, consumer -> {
			try {
				URL u = new URL(url);

				long start = System.currentTimeMillis();

				URLConnection connection = u.openConnection();
				connection.connect();

				return System.currentTimeMillis() - start;
			} catch (IOException e) {
				consumer.accept(e);
				return -1L;
			}
		});
	}

}
