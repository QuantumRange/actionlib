package de.quantumrange.actionlib.impl.actions;

import de.quantumrange.actionlib.ActionManager;
import de.quantumrange.actionlib.impl.manager.RateLimitedThreadManager;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;

public class RateLimitedAction<T> extends NormalAction<T> {

	private final int id;

	/**
	 * @param manager a instance of a ActionManager implementation (e.g.: {@link NormalAction},
	 * {@link RateLimitedThreadManager})
	 * @param id the RateID
	 * @param function the Action
	 */
	public RateLimitedAction(@Nonnull ActionManager manager,
							 int id,
							 @Nonnull Function<Consumer<Throwable>, T> function) {
		super(manager, function);
		this.id = id;
	}

	/**
	 * Return the RateID
	 *
	 * @return the RateLimited ID that matches the action.
	 * @see RateLimitedThreadManager
	 */
	public int getId() {
		return id;
	}
}
