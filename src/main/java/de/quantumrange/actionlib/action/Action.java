package de.quantumrange.actionlib.action;

import de.quantumrange.actionlib.action.impl.manager.ActionThread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Action<T> {

    /**
     * Executes the action in the background.
     */
    default void queue() {
        queue(null);
    }

    /**
     * Execute the Action after the time.
     *
     * @param after is the time count.
     * @param unit is the time unit.
     */
    default void queue(long after, TimeUnit unit) {
        deadline(after, unit).queue();
    }

    /**
     * Execute the Action in the background.
     *
     * @param completion is executed as soon as the value has been calculated.
     */
    default void queue(@Nullable Consumer<T> completion) {
        queue(completion, null);
    }

    /**
     * Execute Action in the background with custom rules depending on the implementation.
     *
     * @param completion is executed as soon as the value has been calculated.
     * @param failure is executed as soon as the value throw a error.
     */
    void queue(@Nullable Consumer<T> completion, @Nullable Consumer<Throwable> failure);

    /**
     * Executes the action and stops the program until the action is finished.
     *
     * @return the calculated value
     */
    T completion();

    /**
     * Just before execution it checks the BooleanSupplier check and if the result is false the action will not be
     * executed. This is useful if you want to check if you have an internet connection just before executing the
     * action.
     *
     * @param check boolCheck
     * @return itself
     */
    Action<T> setCheck(@Nullable BooleanSupplier check);

    /**
     * Adding another Check to the Action.
     *
     * @param check is a additional Check.
     * @return itself
     */
    @Nonnull
    default Action<T> addCheck(@Nonnull BooleanSupplier check) {
        BooleanSupplier supplier = getCheck();
        return setCheck(() -> (supplier == null || supplier.getAsBoolean()) && check.getAsBoolean());
    }

    /**
     * Return the Current Check, if no check is set return null
     *
     * @return check - boolCheck
     */
    @Nullable
    BooleanSupplier getCheck();

    /**
     * Getter for the current Deadline.
     *
     * @return this current Deadline
     */
    @Nullable
    LocalDateTime getDeadline();

    /**
     * Set the Deadline for the Action
     *
     * @param deadline is the time from which the action can be executed.
     * @return itself
     */
    @Nonnull
    Action<T> deadline(@Nonnull LocalDateTime deadline);

    /**
     * Set a deadline with the current time and additional time.
     *
     * @param delay the time count.
     * @param unit the time unit.
     * @return itself
     */
    @Nonnull
    default Action<T> deadline(long delay, @Nonnull TimeUnit unit) {
        return deadline(LocalDateTime.now().plusNanos(unit.toNanos(delay)));
    }

    /**
     * This executes the code in the ActionManager.
     *
     * @param thread This is the action thread where the action is currently running.
     * @param consumer Throw this Exception if
     * @return If the action was a completion action, then the calculated value is returned, otherwise null.
     */
    @Nullable
    T submit(@Nullable ActionThread thread, @Nullable Consumer<Throwable> consumer);
}
