package br.eti.souza.server;

import br.eti.souza.exception.SystemException;
import br.eti.souza.logger.Logger;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Representa um Servidor
 * @author Alan Moraes Souza
 */
public class Server {

    /** Define que servidor deve parar. */
    private static ServerSocket INSTANCE;
    /** Gerenciador de Handlers para os requests. */
    protected static final HandlerManager HANDLER_MANAGER = new HandlerManager();

    /**
     * Adciona um handler ao sistema, substitui se o path já estiver em uso.
     * @param path Path do Handler. Paths são case sensitives, devem iniciar com "/", variaveis devem ser definidas entre chaves "{nomeVariavel}. Ex.: "/teste/{qtd}" e " /teste/{qtd}/ " são iguais e validos.
     * @param handler Handler usado para o Path.
     * @throws SystemException Caso o Path, Method ou Handler sejam inválidos.
     */
    public static void addHandler(String path, Handler handler) throws SystemException {
        Server.HANDLER_MANAGER.addHandler(path, handler);
    }

    /**
     * Inicia servidor na porta especificada.
     * @param port Porta a ser usada.
     * @throws SystemException Caso ocorra erro na execução do servidor.
     */
    public static void start(int port) throws SystemException {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(port)) {
                Server.INSTANCE = server;
                while (!server.isClosed()) {
                    new ProcessConnection(server.accept()).start();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Logger.error(new SystemException("could.not.use.thread.sleep.for.server.requests", e));
                    }
                }
            } catch (IOException e) {
                Logger.error(new SystemException("error.in.server.socket", e));
            }
        }).start();
        Logger.info("server.started");
    }

    /** Informa que o servidor deve parar na proxima requisição. */
    public static void stop() {
        if (Server.INSTANCE == null) {
            Logger.info("server.is.not.running");
            Logger.info("using.system.exit.0");
            System.exit(0);
        } else if (Server.INSTANCE.isClosed()) {
            Logger.info("server.already.stoping");
            Logger.info("using.system.exit.1");
            System.exit(1);
        } else {
            try {
                Logger.info("server.will.stop.soon");
                Server.INSTANCE.close();
            } catch (IOException e) {
                Logger.error(new SystemException("could.not.stop.server", e));
                Logger.info("using.system.exit.2");
                System.exit(2);
            }
        }
    }
}
