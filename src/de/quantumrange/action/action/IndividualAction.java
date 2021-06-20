package de.quantumrange.action.action;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IndividualAction<T> implements Action<T> {

    private final Function<Consumer<Throwable>, T> supplier;
    private LocalDateTime deadline;

    public IndividualAction(Function<Consumer<Throwable>, T> supplier) {
        this.supplier = supplier;
        this.deadline = LocalDateTime.MIN;
    }

    @Override
    public void queue() {
        queue(null);
    }

    @Override
    public void queue(Consumer<T> completion) {
        queue(completion, null);
    }

    @Override
    public void queue(Consumer<T> completion, Consumer<Throwable> failure) {
        new Thread(() -> {
            while (deadline.isAfter(LocalDateTime.now())) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) { }
            }
            try {
                T value = supplier.apply(err -> {
                    if (failure != null) failure.accept(err);
                });
                if (completion != null) completion.accept(value);
            } catch (Exception e) {
                if (failure != null) failure.accept(e);
            }
        }).start();
    }

    @Override
    public T completion() {
        while (deadline.isAfter(LocalDateTime.now())) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) { }
        }
        return supplier.apply(err -> {});
    }

    @Override
    public Action<T> deadline(LocalDateTime deadline) {
        this.deadline = deadline;
        return this;
    }

}
