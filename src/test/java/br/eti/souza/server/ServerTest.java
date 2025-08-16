package br.eti.souza.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Testes da classe br.eti.souza.server.Server.
 * @author Alan Moraes Souza
 */
public class ServerTest {

    /** Incia o servidor. */
    @BeforeAll
    public static void beforeAll() {
        Server.start();
    }

    /** Verfica se a porta subiu como configurado. */
    @Test
    public void checkPort() {
        Assertions.assertEquals(7070, Server.getPort());
    }
}