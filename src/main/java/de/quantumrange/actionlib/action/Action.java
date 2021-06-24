package de.quantumrange.actionlib.action;

import javax.annotation.Nonnull;
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
    default void queue(Consumer<T> completion) {
        queue(completion, null);
    }

    /**
     * Execute Action in the background with custom rules depending on the implementation.
     *
     * @param completion is executed as soon as the value has been calculated.
     * @param failure is executed as soon as the value throw a error.
     */
    void queue(Consumer<T> completion, Consumer<Throwable> failure);

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
    Action<T> setCheck(BooleanSupplier check);

    default Action<T> addCheck(BooleanSupplier check) {
        BooleanSupplier supplier = getCheck();
        setCheck((supplier == true || supplier.getAsBoolean()) && check.getAsBoolean());
    }

    /**
     * @return check - boolCheck
     */
    @Nonnull
    BooleanSupplier getCheck();

    /**
     * Set the Deadline for the Action
     *
     * @param deadline is the time from which the action can be executed.
     * @return itself
     */
    Action<T> deadline(LocalDateTime deadline);

    /**
     * Map the Result Value to a Different Type.
     *
     * @param map the Function that map T to O
     * @param <O> the new Type
     * @return itself with other return value
     */
    <O> Action<O> map(Function<O, T> map);

    /**
     * Set a deadline with the current time and additional time.
     *
     * @param delay the time count.
     * @param unit the time unit.
     * @return itself
     */
    default Action<T> deadline(long delay, TimeUnit unit) {
        return deadline(LocalDateTime.now().plusNanos(unit.toNanos(delay)));
    }

}
