package de.quantumrange.actionlib.impl.actions;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.ActionManager;
import de.quantumrange.actionlib.impl.manager.RateLimitedThreadManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;
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

	protected RateLimitedAction(@Nonnull ActionManager manager, @Nonnull LocalDateTime deadline,
							 @Nullable Consumer<T> completion, @Nullable Consumer<Throwable> failure,
							 @Nullable BooleanSupplier check, @Nonnull Function<Consumer<Throwable>, T> function, int id) {
		super(manager, deadline, completion, failure, check, function);
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

	@Override
	public <O> Action<O> map(Function<T, O> map) {
		return new RateLimitedAction<>(manager, deadline, null, failure, check,
				throwable -> map.apply(function.apply(throwable)), id);
	}
}
