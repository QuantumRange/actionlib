package de.quantumrange.action.web;

import de.quantumrange.action.action.Action;

import java.util.function.Function;
import java.util.function.Supplier;

public interface WebRequest<T, J> {

    WebRequest<T, J> setHeader(String key, String value);
    WebRequest<T, J> setAction(Function<Supplier<T>, Action<T>> action);
    WebRequest<T, J> setData(J data);
    Action<T> connect();

}
