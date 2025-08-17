package br.eti.souza.server;

import br.eti.souza.configuration.Configuration;
import br.eti.souza.exception.SystemException;
import br.eti.souza.exception.UserException;
import br.eti.souza.i18n.Message;
import br.eti.souza.i18n.Messages;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wrapper com.sun.net.httpserver.HttpServer
 * @author Alan Moraes Souza
 */
public class Server {

    /** Logger para esta classe. */
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
    /** Verdadeiro servidor HTTP. */
    private static HttpServer SERVER;

    /** Cria o objeto do servidor. */
    static {
        try {
            Server.SERVER = HttpServer.create(new InetSocketAddress(Configuration.get("server.port", 9090)), 0);
            Server.configureBaseContext();
        } catch (IllegalArgumentException | IOException e) {
            Server.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Inclui um serviço no servidor.
     * @param path Caminho do serviço.
     * @param service Serviço.
     */
    public static void addService(String path, Service service) {
        //Server.SERVER.removeContext(path);
        Server.SERVER.createContext(path, service);
    }

    /**
     * Inclui um pasta estática no servidor.
     * @param path Caminho do serviço.
     * @param folder Caminho da pasta.
     */
    private static void configureBaseContext() {
        Server.addService("/", new Service() {
            private final static Map<String, Response> CACHE = new HashMap<>();
            private final static boolean USE_CACHE = Configuration.get("server.use.cache.on.static.folder", true);
            private final static String FOLDER = Configuration.get("server.static.folder");
            @Override
            protected Response get(Request request) throws SystemException, UserException {
                if (FOLDER != null && !request.getPath().contains("..")) {
                    if (CACHE.containsKey(request.getPath())) {
                        System.out.println("COM CACHE");
                        return CACHE.get(request.getPath());
                    } else {
                        System.out.println("SEM CACHE");
                        var path = Path.of(FOLDER + request.getPath());
                        if (Files.exists(path)) {
                            try {
                                var response = Response.create().statusCode(200).body(Files.readAllBytes(path));
                                if (USE_CACHE) {
                                    CACHE.put(request.getPath(), response);
                                }
                                return response;
                            } catch (IOException e) {
                                throw new SystemException(new Message("unable.to.read.file.from.static.folder"), e);
                            }
                        }
                    }
                }
                var body = "{ \"server\": \"".concat(Configuration.get("server.name")).concat("\", \"message\": \"").concat(Messages.get("page.not.found")).concat("\" }").getBytes();
                return Response.create().statusCode(404).body(body);
            }
        });
    }

    /** Iniciar o servidor. */
    public static void start() {
        Server.SERVER.start();
    }
}
