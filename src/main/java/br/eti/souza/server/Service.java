package br.eti.souza.server;

import br.eti.souza.configuration.Configuration;
import br.eti.souza.exception.SystemException;
import br.eti.souza.exception.UserException;
import br.eti.souza.i18n.Messages;
import br.eti.souza.json.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Representa um serviço HTTP.
 * @author Alan Moraes Souza
 */
public abstract class Service implements HttpHandler {

    /**
     * Entrada da execução do serviço..
     * @param exchange HttpExchange da conexão HTTP.
     */
    @Override
    public void handle(HttpExchange exchange) {
        Response response;
        var request = Request.create(exchange);
        try {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                response = this.get(request);
            } else if ("POST".equalsIgnoreCase(request.getMethod())) {
                response = this.post(request);
            } else if ("PUT".equalsIgnoreCase(request.getMethod())) {
                response = this.put(request);
            } else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
                response = this.delete(request);
            } else {
                var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(Messages.get("method.not.allow")).concat("\" }").getBytes();
                response = Response.create().statusCode(405).body(body);
            }
        } catch (SystemException e) {
            var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(e.getLocalizedMessage()).concat("\" }").getBytes();
            response = Response.create().statusCode(500).body(body);
        } catch (UserException e) {
            var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"messages\": ").concat(JSON.toJSON(e.getLocalizedMessages())).concat(" }").getBytes();
            response = Response.create().statusCode(400).body(body);
        }
        response.write(exchange);
    }

    /**
     * Processa requisção com método GET.
     * @param request Requisição HTTP.
     * @return Resposta HTTP da requisição.
     * @throws SystemException Caso ocorra erro causado pelo sistema.
     * @throws UserException Caso ocorra erro causado pelo usuário.
     */
    protected Response get(Request request) throws SystemException, UserException {
        var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(Messages.get("not.implemented")).concat("\" }").getBytes();
        return Response.create().statusCode(501).body(body);
    }

    /**
     * Processa requisção com método GET.
     * @param request Requisição HTTP.
     * @return Resposta HTTP da requisição.
     * @throws SystemException Caso ocorra erro causado pelo sistema.
     * @throws UserException Caso ocorra erro causado pelo usuário.
     */
    protected Response post(Request request) throws SystemException, UserException {
        var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(Messages.get("not.implemented")).concat("\" }").getBytes();
        return Response.create().statusCode(501).body(body);
    }

    /**
     * Processa requisção com método GET.
     * @param request Requisição HTTP.
     * @return Resposta HTTP da requisição.
     * @throws SystemException Caso ocorra erro causado pelo sistema.
     * @throws UserException Caso ocorra erro causado pelo usuário.
     */
    protected Response put(Request request) throws SystemException, UserException {
        var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(Messages.get("not.implemented")).concat("\" }").getBytes();
        return Response.create().statusCode(501).body(body);
    }

    /**
     * Processa requisção com método GET.
     * @param request Requisição HTTP.
     * @return Resposta HTTP da requisição.
     * @throws SystemException Caso ocorra erro causado pelo sistema.
     * @throws UserException Caso ocorra erro causado pelo usuário.
     */
    protected Response delete(Request request) throws SystemException, UserException {
        var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(Messages.get("not.implemented")).concat("\" }").getBytes();
        return Response.create().statusCode(501).body(body);
    }
}
