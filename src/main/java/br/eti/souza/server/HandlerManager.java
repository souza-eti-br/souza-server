package br.eti.souza.server;

import br.eti.souza.exception.SystemException;
import br.eti.souza.exception.UserException;
import br.eti.souza.logger.Logger;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por gerenciar os Handlers.
 * @author Alan Moraes Souza
 */
public final class HandlerManager {

    /** Caracter separador de Paths. */
    private static final String PATH_SEPARATOR = "/";
    /** Caracter inicial de variável do path. */
    private static final String VAR_PATH_INI = "{";
    /** Caracter final de variável do path. */
    private static final String VAR_PATH_END = "}";
    /** PathPart inicial, do path até "/". */
    private final PathPart partPath = new PathPart();

    /** Construtor padrão. */
    public HandlerManager() {
    }

    /**
     * Adciona um handler ao sistema, substitui se o path já estiver em uso.
     * @param path Path do Handler. Paths são case sensitives, devem iniciar com "/", variaveis devem ser definidas entre chaves "{nomeVariavel}. Ex.: "/teste/{qtd}" e " /teste/{qtd}/ " são iguais e validos.
     * @param handler Handler usado para o Path.
     * @throws SystemException Caso o Path, Method ou Handler sejam inválidos.
     */
    protected void addHandler(String path, Handler handler) throws SystemException {
        if (handler == null) {
            throw new SystemException("handler.cannot.be.null");
        }
        if ((path == null) || path.isBlank() || path.contains(" ") || path.contains(HandlerManager.PATH_SEPARATOR + HandlerManager.PATH_SEPARATOR)) {
            throw new SystemException("path.is.invalid");
        }
        path = path.trim();
        if (!path.startsWith(HandlerManager.PATH_SEPARATOR)) {
            throw new SystemException("path.must.start.with.separator");
        } else if (path.equals(HandlerManager.PATH_SEPARATOR)) {
            this.partPath.handler = handler;
        } else {
            PathPart pathPart = this.partPath;
            String[] subPaths = path.substring(1).split(HandlerManager.PATH_SEPARATOR);
            List<String> varNames = new ArrayList<>();
            for (String subPath : subPaths) {
                if (subPath.startsWith(HandlerManager.VAR_PATH_INI) && subPath.endsWith(HandlerManager.VAR_PATH_END)) {
                    String varName = subPath.substring(1, subPath.length() - 1);
                    if (varNames.contains(varName)) {
                        throw new SystemException("path.param.must.be.unique.per.path");
                    }
                    varNames.add(varName);
                    if (!pathPart.pathParts.containsKey("{varName}")) {
                        pathPart.pathParts.put("{varName}", new PathPart());
                    }
                    pathPart = pathPart.pathParts.get("{varName}");
                } else {
                    if (!pathPart.pathParts.containsKey(subPath)) {
                        pathPart.pathParts.put(subPath, new PathPart());
                    }
                    pathPart = pathPart.pathParts.get(subPath);
                }
            }
            if (pathPart.handler != null) {
                throw new SystemException("handler.for.path.already.exists");
            } else {
                pathPart.handler = handler;
                if (!varNames.isEmpty()) {
                    pathPart.varNames.addAll(varNames);
                }
            }
        }
    }

    /**
     * Obter Handler correspondente a requisição.
     * @param request Requisição.
     * @return Handler correspondente.
     */
    protected Response execute(Request request) {
        try {
            if (request == null || (request.getPath().isEmpty() && request.getMethod().isEmpty() && request.getVersion().isEmpty())) {
                return Response.build(400, "Bad Request").body("{ \"name\": \"Souza Server\", \"message\": \"empty.request\" }");
            } else if (request.getPath().isEmpty()) {
                return Response.build(400, "Bad Request").body("{ \"name\": \"Souza Server\", \"message\": \"empty.path\" }");
            } else if (request.getMethod().isEmpty()) {
                return Response.build(400, "Bad Request").body("{ \"name\": \"Souza Server\", \"message\": \"empty.method\" }");
            } else if (request.getVersion().isEmpty()) {
                return Response.build(400, "Bad Request").body("{ \"name\": \"Souza Server\", \"message\": \"empty.version\" }");
            } else if (request.getPath().equals(HandlerManager.PATH_SEPARATOR) && (this.partPath.handler != null)) {
                String method = request.getMethod().toUpperCase();
                if ("GET".equalsIgnoreCase(method)) {
                    return this.partPath.handler.get(request);
                } else if ("POST".equalsIgnoreCase(method)) {
                    return this.partPath.handler.post(request);
                } else if ("PUT".equalsIgnoreCase(method)) {
                    return this.partPath.handler.put(request);
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    return this.partPath.handler.delete(request);
                } else {
                    return Response.build(405, "Method Not Allowed").body("{ \"name\": \"Souza Server\", \"message\": \"method.not.allowed\" }");
                }
            } else {
                PathPart pathPart = this.partPath;
                String[] subPaths = request.getPath().trim().substring(1).split(HandlerManager.PATH_SEPARATOR);
                List<String> varValues = new ArrayList<>();
                for (String subPath : subPaths) {
                    if (pathPart.pathParts.containsKey(subPath)) {
                        pathPart = pathPart.pathParts.get(subPath);
                    } else if (pathPart.pathParts.containsKey("{varName}")) {
                        pathPart = pathPart.pathParts.get("{varName}");
                        varValues.add(subPath);
                    } else {
                        return Response.build(404, "Not Found").body("{ \"name\": \"Souza Server\", \"message\": \"handler.not.found\" }");
                    }
                }
                if (pathPart.handler != null) {
                    if (!varValues.isEmpty()) {
                        if (varValues.size() != pathPart.varNames.size()) {
                            throw new SystemException("wrong.path.variables.configuration");
                        }
                        for (int i = 0; i < varValues.size(); i++) {
                            request.addPathParam(pathPart.varNames.get(i), varValues.get(i));
                        }
                    }
                    String method = request.getMethod().toUpperCase();
                    if ("GET".equalsIgnoreCase(method)) {
                        return pathPart.handler.get(request);
                    } else if ("POST".equalsIgnoreCase(method)) {
                        return pathPart.handler.post(request);
                    } else if ("PUT".equalsIgnoreCase(method)) {
                        return pathPart.handler.put(request);
                    } else if ("DELETE".equalsIgnoreCase(method)) {
                        return pathPart.handler.delete(request);
                    } else {
                        return Response.build(405, "Method Not Allowed").body("{ \"name\": \"Souza Server\", \"message\": \"method.not.allowed\" }");
                    }
                }
            }
            return Response.build(404, "Not Found").body("{ \"name\": \"Souza Server\", \"message\": \"handler.not.found\" }");
        } catch (UserException e) {
            return Response.build(412, "Precondition Failed").body("{ \"name\": \"Souza Server\", \"messages\": " + new Gson().toJson(e.getMessages()) + " }");
        } catch (SystemException e) {
            Logger.error(e);
            return Response.build(500, "Internal Server Error").body("{ \"name\": \"Souza Server\", \"message\": \"internal.server.error\" }");
        }
    }
}
