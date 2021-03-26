package br.eti.souza.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa uma requisição.
 * @author Alan Moraes Souza
 */
public class Request {

    /** Método HTTP da requisição. */
    private final String method;
    /** Versão HTTP da requisição. */
    private final String version;
    /** Cabeçalhos HTTP da requisição. */
    private final Map<String, List<String>> headers;
    /** Caminho HTTP da requisição. */
    private final String path;
    /** Parâmetros QueryString HTTP da requisição. */
    private final Map<String, List<String>> queryParams;
    /** Corpo da requisição. */
    private final byte[] body;
    /** Parâmetros QueryString HTTP da requisição. */
    private final Map<String, String> pathParams = new HashMap<>();

    /**
     * Construtor que define todos os dados da requisição.
     * @param method Método HTTP da requisição.
     * @param version Versão HTTP da requisição.
     * @param headers Cabeçalhos HTTP da requisição.
     * @param path Caminho HTTP da requisição.
     * @param queryParams Parâmetros QueryString HTTP da requisição.
     * @param body Corpo da requisição.
     */
    protected Request(String method, String version, Map<String, List<String>> headers, String path, Map<String, List<String>> queryParams, byte[] body) {
        this.method = (method == null ? "" : method);
        this.version = (version == null ? "" : version);
        this.headers = (headers == null ? new HashMap<>() : headers);
        this.path = (path == null ? "" : path);
        this.queryParams = (queryParams == null ? new HashMap<>() : queryParams);
        this.body = (body == null ? new byte[0] : body);
    }

    /**
     * Retorna o método da requisção.
     * @return Método HTTP da requisição.
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Retorna a versão da requisção.
     * @return Versão HTTP da requisição.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Retorna os cabeçalhos da requisção.
     * @return Cabeçalhos HTTP da requisição.
     */
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    /**
     * Retorna cabeçalho da requisção.
     * @param name Nome do cabeçalho.
     * @return Cabeçalho HTTP da requisição.
     */
    public List<String> getHeaders(String name) {
        return this.headers.get(name);
    }

    /**
     * Retorna primeiro valor do cabeçalho da requisção.
     * @param name Nome do cabeçalho.
     * @return Primeiro valor do cabeçalho da requisção.
     */
    public String getHeader(String name) {
        if (this.headers.containsKey(name)) {
            return this.headers.get(name).get(0);
        } else {
            return null;
        }
    }

    /**
     * Retorna o caminho da requisção.
     * @return Caminho HTTP da requisição.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Retorna parâmetros QueryString da requisção.
     * @return Parâmetros QueryString da requisção.
     */
    public Map<String, List<String>> getQueryParams() {
        return this.queryParams;
    }

    /**
     * Retorna parâmetro QueryString da requisção.
     * @param name Nome do parâmetro.
     * @return Parâmetro QueryString da requisção.
     */
    public List<String> getQueryParams(String name) {
        return this.queryParams.get(name);
    }

    /**
     * Retorna primeiro valor do parâmetro QueryString da requisção.
     * @param name Nome do parâmetro.
     * @return Primeiro valor do parâmetro QueryString da requisção.
     */
    public String getQueryParam(String name) {
        if (this.queryParams.containsKey(name)) {
            return this.queryParams.get(name).get(0);
        } else {
            return null;
        }
    }

    /**
     * Retorna o corpo da requisção.
     * @return Corpo da requisição.
     */
    public byte[] getBody() {
        return this.body;
    }

    /**
     * Retorna o corpo da requisção como texto.
     * @return Corpo da requisição como texto.
     */
    public String getBodyAsString() {
        return new String(this.body);
    }

    /**
     * Adiciona parâmetros de path da requisção.
     * @param name Nome do parâmetro.
     * @param value Valor do parâmetro.
     */
    protected void addPathParam(String name, String value) {
        this.pathParams.put(name, value);
    }

    /**
     * Retorna parâmetros de path da requisção.
     * @return Parâmetros de path da requisção.
     */
    public Map<String, String> getPathParams() {
        return this.pathParams;
    }

    /**
     * Retorna primeiro valor do parâmetro de path da requisção.
     * @param name Nome do parâmetro.
     * @return Primeiro valor do parâmetro de path da requisção.
     */
    public String getPathParam(String name) {
        if (this.pathParams.containsKey(name)) {
            return this.pathParams.get(name);
        } else {
            return null;
        }
    }
}
