package br.eti.souza.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa uma resposta para uma requisição.
 * @author Alan Moraes Souza
 */
public class Response {

    /** Código de status HTTP. */
    private final Integer statusCode;
    /** Mensagem de status HTTP. */
    private final String statusMessage;
    /** Cabeçalhos da resposta. */
    private final Map<String, String> headers = new HashMap<>();
    /** Corpo da resposta. */
    private byte[] body = null;

    /**
     * Construtor uma Resposta com código e mensagem HTTP.
     * @param statusCode Código de status HTTP.
     * @param statusMessage Mensagem de status HTTP.
     */
    private Response(Integer statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    /**
     * Cria uma Resposta com status HTTP 200/OK.
     * @return Resposta com status HTTP 200/OK.
     */
    public static Response build() {
        return new Response(200, "OK");
    }

    /**
     * Cria uma Resposta com status HTTP de acordo com os parâmetros.
     * @param statusCode Código de status HTTP.
     * @param statusMessage Mensagem de status HTTP.
     * @return Resposta com status de acordo com os parâmetros.
     */
    public static Response build(Integer statusCode, String statusMessage) {
        return new Response(statusCode, statusMessage);
    }

    /**
     * Modifica o corpo da resposta de acordo com os parâmetros.
     * @param body Bytes do arquivo de resposta.
     * @param filename Nome do arquivo da resposta.
     * @return Resposta com o corpo alterado.
     */
    public Response body(byte[] body, String filename) {
        this.body = body;
        this.headers.put("Content-Type", "application/octet-stream");
        this.headers.put("Content-Disposition", "attachment; filename=" + filename);
        return this;
    }

    /**
     * Modifica o corpo da resposta de acordo com os parâmetros.
     * @param body JSON da resposta.
     * @return Resposta com o corpo alterado.
     */
    public Response body(String body) {
        this.body = body.getBytes();
        this.headers.put("Content-Type", "text/json");
        return this;
    }

    /**
     * Adiciona um cabeçario a resposta, caso exista, substituio valor anterior.
     * @param name Nome do cabeçario.
     * @param value Valor do cabeçario.
     * @return Resposta com o cabeçario adcionado.
     */
    public Response header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * Retorna o código do status HTTP da resposta.
     * @return Código do status HTTP da resposta.
     */
    public Integer getStatusCode() {
        return this.statusCode;
    }

    /**
     * Retorna a mensagem do status HTTP da resposta.
     * @return Mensagem do status HTTP da resposta.
     */
    public String getStatusMessage() {
        return this.statusMessage;
    }

    /**
     * Retorna os cabeçalhos da resposta.
     * @return Cabeçalhos da resposta.
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Retorna o corpo da resposta.
     * @return Corpo da resposta.
     */
    public byte[] getBody() {
        return this.body;
    }
}
