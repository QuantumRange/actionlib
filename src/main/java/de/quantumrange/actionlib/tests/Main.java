package de.quantumrange.actionlib.tests;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.ActionManager;
import de.quantumrange.actionlib.impl.actions.RateLimitedAction;
import de.quantumrange.actionlib.impl.manager.RateLimitedThreadManager;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Main {

	public static void main(String[] args) {
		RateLimitedThreadManager threadManager = new RateLimitedThreadManager(.5f);
		threadManager.registerRateLimit(5, 1000);

		for (int i = 0; i < 50; i++) {
			generateSomething(threadManager).queue(time -> System.out.printf("It takes %d milliseconds to calculate 5000 times sqrt.%n", time));
		}

		System.out.println("Start Calculate...");
	}

	public static Action<Long> generateSomething(ActionManager am) {
		AtomicLong start = new AtomicLong(-1L);
		return new RateLimitedAction<>(am, 5, throwable -> {
			double s = Double.MAX_VALUE;

			for (int i = 0; i < 5_000; i++) {
				s = Math.sqrt(s);
			}

			return System.currentTimeMillis() - start.get();
		}).setCheck(() -> {
			start.set(System.currentTimeMillis());
			return true;
		});
	}

}
