package br.eti.souza.server;

import br.eti.souza.i18n.Messages;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representa uma resposta HTTP.
 * @author Alan Moraes Souza
 */
public class Response {

    /** Logger para esta classe. */
    private final static Logger LOGGER = Logger.getLogger(Response.class.getName());

    /** Código de status http da resposta. */
    private int statusCode = 200;

    /** Corpo da resposta. */
    private byte[] body = new byte[0];

    /** Tipo de conteúdo da resposta. */
    private String contentType = "application/json";

    /**
     * Cria uma resposta HTTP.
     * @return Resposta HTTP.
     */
    public static Response create() {
        return new Response();
    }

    /**
     * Define código de status http da resposta.
     * @param code Código de status http da resposta.
     * @return A propria resposta.
     */
    public Response statusCode(int code) {
        this.statusCode = code;
        return this;
    }

    /**
     * Define tipo do conteúdo da resposta.
     * @param contentType Tipo do conteúdo da resposta.
     * @return A propria resposta.
     */
    public Response contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Define corpo da resposta.
     * @param body Corpo da resposta.
     * @return A propria resposta.
     */
    public Response body(byte[] body) {
        this.body = body;
        return this;
    }

    /**
     * Escreve a resposta HTTP.
     * @param exchange HttpExchange da conexão HTTP.
     */
    public void write(HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(this.statusCode, this.body.length);
            exchange.getResponseHeaders().add("Content-Type", this.contentType);
            exchange.getResponseBody().write(this.body);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, Messages.get("unable.to.write.http.response"), e);
        }
    }
}
