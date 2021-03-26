package br.eti.souza.server;

import br.eti.souza.exception.SystemException;
import br.eti.souza.exception.UserException;

/**
 * Processa uma requisição
 * @author Alan Moraes Souza
 */
public abstract class Handler {

    /**
     * Processar requisição GET (Obter).
     * @param request Requisição.
     * @return Resposta para a requisição.
     * @throws SystemException Caso ocorra SystemException (Gera response com código HTTP 500).
     * @throws UserException Caso ocorra UserException (Gera response com código HTTP 400).
     */
    public Response get(Request request) throws SystemException, UserException {
        return Response.build(501, "Not Implemented").body("{ \"name\": \"Souza Server\", \"message\": \"not.implemented\" }");
    }

    /**
     * Processar requisição POST (Criar).
     * @param request Requisição.
     * @return Resposta para a requisição.
     * @throws SystemException Caso ocorra SystemException (Gera response com código HTTP 500).
     * @throws UserException Caso ocorra UserException (Gera response com código HTTP 400).
     */
    public Response post(Request request) throws SystemException, UserException {
        return Response.build(501, "Not Implemented").body("{ \"name\": \"Souza Server\", \"message\": \"not.implemented\" }");
    }

    /**
     * Processar requisição PUT (Atualizar).
     * @param request Requisição.
     * @return Resposta para a requisição.
     * @throws SystemException Caso ocorra SystemException (Gera response com código HTTP 500).
     * @throws UserException Caso ocorra UserException (Gera response com código HTTP 400).
     */
    public Response put(Request request) throws SystemException, UserException {
        return Response.build(501, "Not Implemented").body("{ \"name\": \"Souza Server\", \"message\": \"not.implemented\" }");
    }

    /**
     * Processar requisição DELETE (Apagar).
     * @param request Requisição.
     * @return Resposta para a requisição.
     * @throws SystemException Caso ocorra SystemException (Gera response com código HTTP 500).
     * @throws UserException Caso ocorra UserException (Gera response com código HTTP 400).
     */
    public Response delete(Request request) throws SystemException, UserException {
        return Response.build(501, "Not Implemented").body("{ \"name\": \"Souza Server\", \"message\": \"not.implemented\" }");
    }
}
