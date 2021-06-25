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

	private @Nonnull
	ActionManager manager;
	private @Nonnull LocalDateTime deadline;
	private @Nullable Consumer<T> completion;
	private @Nullable Consumer<Throwable> failure;
	private @Nullable BooleanSupplier check;
	private final @Nonnull Function<Consumer<Throwable>, T> function;

	public NormalAction(@Nonnull ActionManager manager,
						@Nonnull Function<Consumer<Throwable>, T> function) {
		this.manager = manager;
		this.deadline = LocalDateTime.now();
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

	@Nullable
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
}
