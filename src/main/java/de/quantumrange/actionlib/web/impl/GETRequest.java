package de.quantumrange.actionlib.web.impl;

import de.quantumrange.actionlib.action.Action;
import de.quantumrange.actionlib.action.IndividualAction;
import de.quantumrange.actionlib.web.WebRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GETRequest<T> implements WebRequest<T, HashMap<String, String>> {

    private final HttpClient.Builder clientBuilder;
    private final HttpRequest.Builder requestBuilder;
    private final Function<Function<Consumer<Throwable>, T>, Action<T>> actionBuilder;

    /*
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response =  client.send(request, HttpResponse.BodyHandlers.ofString());
     */

    public GETRequest(URI uri) {
        this.clientBuilder = HttpClient.newBuilder();
        this.requestBuilder = HttpRequest.newBuilder().uri(uri);
        this.actionBuilder = IndividualAction::new;
    }

    public GETRequest(URL url) throws URISyntaxException {
        this(url.toURI());
    }

    public GETRequest(String url) {
        this(URI.create(url));
    }

    @Override
    public WebRequest<T, HashMap<String, String>> setHeader(String key, String value) {
        requestBuilder.header(key, value);
        return null;
    }

    @Override
    public WebRequest<T, HashMap<String, String>> setAction(Function<Supplier<T>, Action<T>> action) {
        return null;
    }

    @Override
    public WebRequest<T, HashMap<String, String>> setData(HashMap<String, String> data) {
        return null;
    }

    @Override
    public Action<T> connect() {
        HttpClient client = clientBuilder.build();
        HttpRequest request = requestBuilder.build();

        return actionBuilder.apply(error -> {
            try {
                return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (IOException | InterruptedException e) {
                error.accept(e);
                return null;
            }
        });
    }

}
