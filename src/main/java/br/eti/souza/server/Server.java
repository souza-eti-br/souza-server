package br.eti.souza.server;

import br.eti.souza.configuration.Configuration;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
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
        } catch (IllegalArgumentException | IOException e) {
            Server.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            System.exit(1);
        }
    }

    /** Iniciar o servidor. */
    public static void start() {
        Server.SERVER.start();
    }

    /**
     * Retorna a porta do servidor.
     * @return Porta do Servidor.
     */
    public static int getPort() {
        return Server.SERVER.getAddress().getPort();
    }
}
