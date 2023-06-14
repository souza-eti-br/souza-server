package br.eti.souza.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resposta HTTP.
 * @author Alan Moraes Souza
 */
public class Response {

    /** Código de status da resposta. */
    private final int statusCode;
    /** Mensagem de status da resposta. */
    private final String statusMessage;
    /** Cabeçalhos da resposta. */
    private final Map<String, String> headers = new HashMap<>();
    /** Conteúdo da resposta. */
    private byte[] contentBody = new byte[0];
    /** Caminho estático da resposta. */
    private Path staticPath = null;
    /** Último momento da recarga do caminho estático da resposta. */
    private long lastReloadStaticPath = 0L;

    /**
     * Construtor que define o código e mensagem do status da resposta.
     * @param statusCode Código do status da resposta.
     * @param statusMessage Mensagem do status da resposta.
     */
    public Response(int statusCode, String statusMessage) {
        this.statusCode = statusCode > 0 ? statusCode : 200;
        this.statusMessage = statusMessage != null ? statusMessage : "OK";
    }

    /**
     * Define o tipo de conteúdo da resposta.
     * @param contentType Tipo de conteúdo da resposta (se nulo usa "text/plain").
     */
    public void setContentType(String contentType) {
        this.headers.put("Content-Type", contentType != null ? contentType : "text/plain");
    }

    /**
     * Define o conteúdo da resposta.
     * @param contentBody Conteúdo da resposta (se nulo usa string vazia).
     */
    public void setContentBody(String contentBody) {
        this.contentBody = contentBody != null ? contentBody.getBytes() : new byte[0];
        this.headers.put("Content-Length", String.valueOf(this.contentBody.length));
    }

    /**
     * Define o conteúdo da resposta.
     * @param contentBody Conteúdo (se nulo usa array vazio).
     */
    public void setContentBody(byte[] contentBody) {
        this.contentBody = contentBody != null ? contentBody : new byte[0];
        this.headers.put("Content-Length", String.valueOf(this.contentBody.length));
    }

    /**
     * Define o caminho stático da resposta.
     * @param staticPath Caminho estático.
     */
    public void setStaticPath(Path staticPath) {
        this.staticPath = staticPath;
        this.lastReloadStaticPath = System.currentTimeMillis();
    }

    /**
     * Retorna o caminho stático da resposta.
     * @return Caminho estático.
     */
    public Path getStaticPath() {
        return this.staticPath;
    }

    /**
     * Reler arquivo do caminho stático da resposta.
     * @throws java.io.IOException Caso não consiga reler o arquivo do caminho estático.
     */
    protected void reloadStaticPath() throws IOException {
        if (this.staticPath != null && ((this.lastReloadStaticPath + 1000) < System.currentTimeMillis())) {
            this.setContentType(Files.probeContentType(this.staticPath));
            this.setContentBody(Files.readAllBytes(this.staticPath));
        }
        this.lastReloadStaticPath = System.currentTimeMillis();
    }

    /**
     * Retorna o status da resposta.
     * @return Status da resposta.
     */
    public String getStatus() {
        if (this.statusMessage.isBlank()) {
            return String.valueOf(this.statusCode);
        } else {
            return this.statusCode + " " + this.statusMessage;
        }
    }

    /**
     * Retorna os nomes dos cabeçalhos da resposta.
     * @return Nomes dos cabeçalhos da resposta.
     */
    public Set<String> getHeaders() {
        return this.headers.keySet();
    }

    /**
     * Verificar se cabeçalho já existe.
     * @param name Nome do cabeçalho.
     * @return Se exite.
     */
    public boolean containsHeader(String name) {
        return this.headers.containsKey(name);
    }

    /**
     * Obter o valor do cabeçalho.
     * @param name Nome do cabeçalho.
     * @return Se exite.
     */
    public String getHeader(String name) {
        return this.headers.get(name);
    }

    /**
     * Incluir um cabeçalho.
     * @param name Nome do cabeçalho.
     * @param value Valor do cabeçalho.
     */
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * Obtem o conteúdo da resposta como byte[].
     * @return Conteúdo da resposta como byte[].
     */
    protected byte[] getBody() {
        return this.contentBody;
    }
}
