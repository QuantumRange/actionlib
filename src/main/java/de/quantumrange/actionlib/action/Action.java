package de.quantumrange.actionlib.action;

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
     * Führt die Action nach der angegebenden Zeit aus.
     *
     * @param after ist die Zeit die es dauern soll bis es queue wird.
     * @param unit ist die TimeUnit in der after gerechnet wird.
     */
    default void queue(long after, TimeUnit unit) {
        deadline(after, unit).queue();
    }

    /**
     * Führt die Action aus und gibt sie per Consumer zurück.
     *
     * @param completion wird ausgeführt sobald im hintergrund alles berechnet wurde.
     */
    default void queue(Consumer<T> completion) {
        queue(completion, null);
    }

    /**
     * Führt die Action aus und gibt sie per Consumer zurück.
     * Bei Fehlern wird failure ausgefüht.
     *
     * @param completion wird ausgeführt sobald im hintergrund alles berechnet wurde.
     * @param failure wird ausgeführt falls es beim Ausführen zum fehler gekommen ist.
     */
    void queue(Consumer<T> completion, Consumer<Throwable> failure);

    /**
     * Führt die Action aus und hält den Computer an bis die Action fertig ausgeführt wurde.
     *
     * @return den Berechneten wert.
     */
    T completion();

    /**
     * Fragt kurz bevorm ausführen noch den BooleanSUpplier check ab und falls der false ist wird die Action nicht
     * weiter ausgeführt. Dies ist Praktisch falls man kurz bevor es ausgeführt wurde nochmal abfragen will ob man
     * gerade z.B: eine internet connection hat.
     *
     * @param check
     * @return
     */
    Action<T> setCheck(BooleanSupplier check);

    /**
     *
     * @param deadline
     * @return
     */
    Action<T> deadline(LocalDateTime deadline);
    <O> Action<O> map(Function<O, T> map);
    default Action<T> deadline(long delay, TimeUnit unit) {
        return deadline(LocalDateTime.now().plusNanos(unit.toNanos(delay)));
    }

}
