package br.eti.souza.server;

import br.eti.souza.exception.SystemException;
import br.eti.souza.logger.Logger;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processa uma requisição.
 * @author Alan Moraes Souza
 */
public class ProcessConnection extends Thread {

    /** Conexão com o cliente. */
    private final Socket connection;

    /**
     * Construtor que define
     * @param connection Conexão com o cliente.
     */
    protected ProcessConnection(Socket connection) {
        this.connection = connection;
    }

    /** Ponto de início da execução desta thread. */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream(), "ISO-8859-1")); OutputStream out = this.connection.getOutputStream()) {
            Request req = this.readRequest(in);
            this.writeResponse(out, Server.HANDLER_MANAGER.execute(req));
        } catch (SystemException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(new SystemException("could.not.use.connect.strem", e));
        }
    }

    /**
     * Cria a requisição apartir do input stream da conexão HTTP.
     * @param in Input stream da conexão HTTP.
     * @return Requisição HTTP.
     * @throws SystemException Caso ocorra erro lendo o input stream.
     */
    private Request readRequest(BufferedReader in) throws SystemException {
        try {
            String line = in.readLine();
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(" ");
                String method = parts[0];
                String path = parts[1];
                Map<String, List<String>> queryParams = new HashMap<>();
                int index = path.indexOf("?");
                if (index > -1) {
                    String[] pairs = URLDecoder.decode(path.substring(index + 1), "UTF-8").trim().split("&");
                    for (String pair : pairs) {
                        String[] param = pair.split("=");
                        if (!queryParams.containsKey(param[0])) {
                            queryParams.put(param[0], new ArrayList<>());
                        }
                        if (param.length == 2) {
                            queryParams.get(param[0]).add(param[1]);
                        }
                    }
                    path = URLDecoder.decode(path.substring(0, index), "UTF-8");
                }
                String version = parts[2];
                Map<String, List<String>> headers = new HashMap<>();
                line = in.readLine();
                int contentLength = 0;
                while (line != null && !line.isEmpty()) {
                    String key, value;
                    index = line.indexOf(": ");
                    if (index == -1) {
                        key = line;
                        value = "";
                    } else {
                        key = line.substring(0, index);
                        value = line.substring(index + 2);
                    }
                    if (!headers.containsKey(key)) {
                        headers.put(key, new ArrayList<>());
                    }
                    headers.get(key).add(value);
                    if ("Content-Length".equalsIgnoreCase(key)) {
                        contentLength = Integer.parseInt(value);
                    }
                    line = in.readLine();
                }
                byte[] body = null;
                if (contentLength > 0) {
                    body = new byte[contentLength];
                    for (int i = 0; i < contentLength; i++) {
                        body[i] = (byte) in.read();
                    }
                }
                return new Request(method, version, headers, path, queryParams, body);
            } else {
                return new Request(null, null, null, null, null, null);
            }
        } catch (IOException e) {
            throw new SystemException("could.not.to.read.request.input.stream", e);
        }
    }

    /**
     * Escreve a resposta HTTP no output stream da conexão.
     * @param out Output stream da conexão HTTP.
     * @param response Resposta HTTP que será devolvida.
     * @throws SystemException Caso ocorra erro escrevendo no output stream.
     */
    private void writeResponse(OutputStream out, Response response) throws SystemException {
        StringBuilder initData = new StringBuilder();
        initData.append("HTTP/1.1 ").append(response.getStatusCode()).append(" ").append(response.getStatusMessage()).append("\r\n");
        initData.append("Server: Souza Server\r\n");
        response.getHeaders().keySet().forEach((key) -> {
            initData.append(key).append(": ").append(response.getHeaders().get(key)).append("\r\n");
        });
        if ((response.getBody() != null) && (response.getBody().length > 0)) {
            initData.append("Content-Length: ").append(response.getBody().length).append("\r\n\r\n");
        } else {
            initData.append("\r\n");
        }
        try {
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            body.write(initData.toString().getBytes());
            if ((response.getBody() != null) && (response.getBody().length > 0)) {
                body.write(response.getBody());
            }
            body.writeTo(out);
            out.flush();
        } catch (IOException e) {
            throw new SystemException("could.not.to.write.response.output.stream", e);
        }
    }
}
