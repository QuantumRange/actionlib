package de.quantumrange.actionlib.impl.actions;

import de.quantumrange.actionlib.Action;
import de.quantumrange.actionlib.ActionManager;
import de.quantumrange.actionlib.impl.manager.ActionThread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class NormalAction<T> implements Action<T> {

	protected @Nonnull ActionManager manager;
	protected @Nonnull LocalDateTime deadline;
	protected @Nullable Consumer<T> completion;
	protected @Nullable Consumer<Throwable> failure;
	protected @Nullable BooleanSupplier check;
	protected final @Nonnull Function<Consumer<Throwable>, T> function;

	public NormalAction(@Nonnull ActionManager manager,
						@Nonnull Function<Consumer<Throwable>, T> function) {
		this.manager = manager;
		this.deadline = LocalDateTime.now();
		this.function = function;
	}

	protected NormalAction(@Nonnull ActionManager manager, @Nonnull LocalDateTime deadline,
					   @Nullable Consumer<T> completion, @Nullable Consumer<Throwable> failure, @Nullable BooleanSupplier check, @Nonnull Function<Consumer<Throwable>, T> function) {
		this.manager = manager;
		this.deadline = deadline;
		this.completion = completion;
		this.failure = failure;
		this.check = check;
		this.function = function;
	}

	@Override
	public void queue(@Nullable Consumer<T> completion, @Nullable Consumer<Throwable> failure) {
		this.completion = completion;
		this.failure = failure;
		manager.queue(this);
	}

	@Override
	public T completion() {
		return manager.completion(this);
	}

	@Override
	public Action<T> setCheck(@Nullable BooleanSupplier check) {
		this.check = check;
		return this;
	}

	@Nullable
	@Override
	public BooleanSupplier getCheck() {
		return this.check;
	}

	@Nonnull
	@Override
	public LocalDateTime getDeadline() {
		return deadline;
	}

	@Nonnull
	@Override
	public Action<T> deadline(@Nonnull LocalDateTime deadline) {
		this.deadline = deadline;
		return this;
	}

	@Nullable
	@Override
	public T submit(@Nullable ActionThread thread, @Nullable Consumer<Throwable> consumer) {
		T result = function.apply(failure != null ? failure : consumer);
		if (completion != null) completion.accept(result);

		return result;
	}

	@Override
	public <O> Action<O> map(Function<T, O> map) {
		return new NormalAction<>(manager, deadline, null, failure, check, throwable -> map.apply(function.apply(throwable)));
	}
}
