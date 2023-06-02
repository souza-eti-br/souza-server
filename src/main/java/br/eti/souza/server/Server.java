package br.eti.souza.server;

import br.eti.souza.exception.I18nMessage;
import br.eti.souza.exception.SystemException;
import br.eti.souza.exception.UserException;
import br.eti.souza.json.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTPServer da Souza.eti.br.
 * @author Alan Moraes Souza
 */
public class Server {

    /** Logger desta classe. */
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    /** ServerSocket que recebe e responde as requisições. */
    private static final ServerSocket SERVER = Server.createServerSocket();
    /** Se servidor esta em execução. */
    private static boolean RUNNING = false;
    /** Mapa dos caminhos estaticos. */
    private static final Map<String, Response> STATIC_PATHS = new HashMap<>();
    /** Mapa dos caminhos para serviços. */
    private static final Map<String, Service> SERVICE_PATHS = new HashMap<>();

    /**
     * Cria o ServerSocket que recebe e responde as requisições.
     * @return ServerSocket que recebe e responde as requisições.
     */
    private static ServerSocket createServerSocket() {
        try {
            return new ServerSocket(8080, 0, InetAddress.getByAddress(new byte[]{0, 0, 0, 0}));
        } catch (IOException e) {
            Server.LOGGER.log(Level.SEVERE, "Não foi possível iniciar o servidor http.", e);
            return null;
        }
    }

    /**
     * Define conteúdo estático, de arquivos existente em resources.
     * @param basePath Caminho base do resources para os arquivos.
     */
    public static void setStaticResource(String basePath) {
        var url = basePath;
        if (basePath.startsWith("/")) {
            try {
                url = Path.of(basePath).toUri().toURL().toString();
            } catch (MalformedURLException e) {
                Server.LOGGER.log(Level.SEVERE, "Caminho base do resources estatico para arquivos está mal formado.", e);
            }
        } else {
            url = Server.class.getClassLoader().getResource(basePath).toString();
        }
        var isJar = url.startsWith("jar:");
        if (isJar) {
            url = url.replaceFirst("jar:", "");
        }
        url = url.replaceFirst("file:", "");
        if (isJar) {
            url = url.substring(0, url.indexOf(".jar!")).concat(".jar");
            try {
                var file = new JarFile(url);
                file.stream().forEach((item) -> {
                    if (!item.isDirectory() && item.getRealName().startsWith(basePath.concat("/"))) {
                        var response = new Response(200, "OK");
                        try {
                            response.setContentType(Files.probeContentType(Path.of(item.getRealName())));
                            response.setContentBody(file.getInputStream(item).readAllBytes());
                        } catch (IOException e) {
                            Server.LOGGER.log(Level.SEVERE, "Não foi possível ler o arquivo no caminho estático para o servidor HTTP. Caminho=[".concat(item.getRealName()).concat("]."), e);
                            response = new Response(500, "Internal Server Error");
                            response.setContentType("text/json");
                            response.setContentBody(JSON.toJSON(new I18nMessage("could.not.read.a.file", item.getRealName())));
                        }
                        var staticPath = item.getRealName().replace(basePath, "");
                        if (!staticPath.startsWith("/")) {
                            staticPath = "/" + staticPath;
                        }
                        Server.STATIC_PATHS.put(staticPath, response);
                        if (staticPath.endsWith("/index.html")) {
                            staticPath = staticPath.substring(0, staticPath.lastIndexOf("index.html"));
                            Server.STATIC_PATHS.put(staticPath, response);
                        }
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (url == null) {
                Server.LOGGER.log(Level.WARNING, "Não existe o caminho especificado para as páginas estáticas do servidor HTTP. Caminho=[".concat(basePath).concat("]."));
                return;
            }
            var directory = Path.of(url);
            if (!Files.isDirectory(directory)) {
                Server.LOGGER.log(Level.WARNING, "O caminho especificado para as páginas estáticas do servidor HTTP não é um diretório. Caminho=[".concat(basePath).concat("]."));
                return;
            }
            Server.addStaticPath(directory.toString(), directory);
        }
    }

    /**
     * Incluir um caminho estático do servidor HTTP.
     * @param base Caminho base.
     * @param path Arquivo ou diretório a ser adcionado.
     */
    private static void addStaticPath(String base, Path path) {
        if (Files.isDirectory(path)) {
            try {
                Files.list(path).forEach((item) -> {
                    Server.addStaticPath(base, item);
                });
            } catch (IOException ex) {
                Server.LOGGER.log(Level.SEVERE, "Não foi possível listar os arquivos no caminho estático para o servidor HTTP. Caminho=[".concat(path.toString()).concat("]."));
            }
        } else {
            var response = new Response(200, "OK");
            try {
                response.setContentType(Files.probeContentType(path));
                response.setContentBody(Files.readAllBytes(path));
            } catch (IOException e) {
                Server.LOGGER.log(Level.SEVERE, "Não foi possível ler o arquivo no caminho estático para o servidor HTTP. Caminho=[".concat(path.toString()).concat("]."));
                response = new Response(500, "Internal Server Error");
                response.setContentType("text/json");
                response.setContentBody(JSON.toJSON(new I18nMessage("could.not.read.a.file", path.toString())));
            }
            var staticPath = path.toString().replace(base, "");
            if (!staticPath.startsWith("/")) {
                staticPath = "/" + staticPath;
            }
            Server.STATIC_PATHS.put(staticPath, response);
            if (staticPath.endsWith("/index.html")) {
                staticPath = staticPath.substring(0, staticPath.lastIndexOf("index.html"));
                Server.STATIC_PATHS.put(staticPath, response);
            }
        }
    }

    /**
     * Incluir um caminho estático do servidor HTTP.
     * @param path Caminho para o serviço.
     * @param service Serviço que será processado.
     */
    public static void addServicePath(String path, Service service) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        Server.SERVICE_PATHS.put(path, service);
    }

    /** Iniciar o servidor. */
    public static void start() {
        Server.RUNNING = true;
        new Thread(() -> {
            while (!Server.SERVER.isClosed()) {
                try {
                    var connection = Server.SERVER.accept();
                    new Thread(() -> {
                        try (var socket = connection) {
                            var request = (Request) null;
                            try {
                                request = new Request(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                                var response = Server.STATIC_PATHS.get(request.getPath());
                                if (response == null) {
                                    if (Server.STATIC_PATHS.containsKey(request.getPath().concat("/"))) {
                                        response = new Response(302, "Found");
                                        response.addHeader("Location", request.getPath().concat("/"));
                                    } else if (Server.SERVICE_PATHS.containsKey(request.getPath())) {
                                        response = Server.SERVICE_PATHS.get(request.getPath()).execute(request);
                                    } else {
                                        response = new Response(404, "Not Found");
                                        response.setContentType("text/json");
                                        response.setContentBody(JSON.toJSON(new I18nMessage("not.found", request.getPath())));
                                    }
                                }
                                Server.writeResponse(request, response, socket.getOutputStream());
                            } catch (UserException e) {
                                var response = new Response(400, "Bad Request");
                                response.setContentType("text/json");
                                response.setContentBody(JSON.toJSON(e));
                                Server.writeResponse(request, response, socket.getOutputStream());
                            } catch (SystemException e) {
                                if (request != null) {
                                    Server.LOGGER.log(Level.SEVERE, "Ocorreu erro processando requisição HTTP para " + request.getFullPath(), e);
                                    var response = new Response(500, "Internal Server Error");
                                    response.setContentType("text/json");
                                    response.setContentBody(JSON.toJSON(e));
                                    Server.writeResponse(request, response, socket.getOutputStream());
                                } else {
                                    Server.LOGGER.log(Level.SEVERE, "Ocorreu erro processando requisição HTTP, request não criado.", e);
                                }
                            }
                        } catch (IOException e) {
                            Server.LOGGER.log(Level.SEVERE, "Ocorreu erro durante a execução de uma conexão do servidor HTTP.", e);
                        }
                    }).start();
                } catch (IOException e) {
                    if (Server.RUNNING) {
                        Server.LOGGER.log(Level.SEVERE, "Ocorreu erro durante a execução do servidor HTTP.", e);
                    } else {
                        Server.LOGGER.log(Level.INFO, "Servidor HTTP parado com sucesso.");
                        System.exit(0);
                    }
                }
            }
        }).start();
        Server.LOGGER.log(Level.INFO, "Servidor HTTP iniciado com sucesso.");
    }

    /** Parar o servidor HTTP e a aplicação java que usa o servidor. */
    public static void stop() {
        try {
            Server.SERVER.close();
        } catch (IOException e) {
            Server.LOGGER.log(Level.SEVERE, "Ocorreu erro ao tentar parar o servidor HTTP.", e);
        } finally {
            Server.RUNNING = false;
        }
    }

    /**
     * Escreve a resposta da requisição HTTP.
     * @param request Requisição HTTP.
     * @param response Resposta da requisição HTTP.
     * @param outputStream OutputStream da conexão HTTP.
     */
    private static void writeResponse(Request request, Response response, OutputStream outputStream) {
        try {
            if (request == null || request.getVersion().isBlank()) {
                outputStream.write(response.getStatus().concat("\n").getBytes());
            } else {
                outputStream.write(request.getVersion().concat(" ").concat(response.getStatus()).concat("\n").getBytes());
            }
            if (!response.containsHeader("Date")) {
                outputStream.write("Date: ".concat(new Date().toString()).concat("\n").getBytes());
            }
            for (var header : response.getHeaders()) {
                outputStream.write(header.concat(": ").concat(response.getHeader(header)).concat("\n").getBytes());
            }
            outputStream.write("\n".getBytes());
            outputStream.write(response.getBody());
        } catch (IOException e) {
            Server.LOGGER.log(Level.SEVERE, "Ocorreu erro escrevendo a resposta HTTP.", e);
        }
    }
}
