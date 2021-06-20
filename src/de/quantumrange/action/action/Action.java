package de.quantumrange.action.action;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface Action<T> {

    void queue();
    void queue(Consumer<T> completion);
    void queue(Consumer<T> completion, Consumer<Throwable> failure);
    T completion();
    Action<T> deadline(LocalDateTime deadline);
    default Action<T> deadline(long delay, TimeUnit unit) {
        return deadline(LocalDateTime.now().plusNanos(unit.toNanos(delay)));
    }

}
