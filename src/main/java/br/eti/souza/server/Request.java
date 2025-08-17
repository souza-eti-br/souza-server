package br.eti.souza.server;

import com.sun.net.httpserver.HttpExchange;

/**
 * Representa uma requisição HTTP.
 * @author Alan Moraes Souza
 */
public class Request {

    /** Caminho da requisição. */
    private String path = "application/json";

    /** Método da requisição. */
    private String method = "GET";

    /**
     * Cria uma resposta HTTP.
     * @param exchange HttpExchange da conexão HTTP.
     * @return Resposta HTTP.
     */
    public static Request create(HttpExchange exchange) {
        var request = new Request();
        request.path = exchange.getRequestURI().toString();
        request.method = exchange.getRequestMethod();
        return request;
    }

    /**
     * Retorna o caminho da requisição.
     * @return Caminho da requisição.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Retorna o método da requisição.
     * @return Método da requisição.
     */
    public String getMethod() {
        return this.method;
    }
}
