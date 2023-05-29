package br.eti.souza.server;

import br.eti.souza.exception.I18nMessage;
import br.eti.souza.exception.SystemException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Requisição HTTP.
 * @author Alan Moraes Souza
 */
public class Request {

    /** Método HTTP da requisição. */
    private final Method method;
    /** Caminho da requisição. */
    private final String path;
    /** QueryString da requisição. */
    private final String query;
    /** Versão da requisição. */
    private final String version;
    /** Cabeçalhos da requisição. */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * Construtor da requisição baseado no BufferedReader do InputStream da requisição.
     * @param reader BufferedReader do InputStream da requisição.
     * @throws SystemException Caso ocorra erro lendo a requisição.
     */
    protected Request(BufferedReader reader) throws SystemException {
        try {
            var time = 0;
            while (!reader.ready()) {
                if (time < 10000) {
                    try {
                        time = time + 100;
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        throw new SystemException(new I18nMessage("error.reading.http.request.could.not.read.first.line"));
                    }
                } else {
                    throw new SystemException(new I18nMessage("error.reading.http.request.could.not.read.first.line.timeout.of.10.seconds"));
                }
            }
            var firstLine = reader.readLine();
            var firstLineParts = firstLine.split(" ");
            if (firstLineParts.length != 3) {
                throw new SystemException(new I18nMessage("error.reading.http.request.first.line.in.wrong.format", firstLine));
            }
            this.method = Method.valueOf(firstLineParts[0]);
            if (firstLineParts[1].indexOf("?") > 0) {
                var position = firstLineParts[1].indexOf("?");
                this.path = firstLineParts[1].substring(0, position);
                this.query = firstLineParts[1].substring(position + 1);
            } else {
                this.path = firstLineParts[1];
                this.query = "";
            }
            this.version = firstLineParts[2];
            while (reader.ready()) {
                var line = reader.readLine();
                var position = line.indexOf(":");
                if (position > 0) {
                    this.headers.put(line.substring(0, position), line.substring(position + 1).trim());
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new SystemException(new I18nMessage("error.reading.http.request"), e);
        }
    }

    /**
     * Obter a versão da requisição.
     * @return Versão da requisição.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Obter o método da requisição.
     * @return Método da requisição.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Obter os cabeçalhos da requisição.
     * @return Cabeçalhos da requisição.
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Obter o caminho da requisição (sem queryString).
     * @return Caminho da requisição (sem queryString)
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Obter o caminho completo da requisição (com queryString).
     * @return Caminho completo da requisição (com queryString).
     */
    public String getFullPath() {
        if (this.query.isBlank()) {
            return this.path;
        } else {
            return this.path + "?" + this.query;
        }
    }
}
