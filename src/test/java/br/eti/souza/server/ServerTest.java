package br.eti.souza.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    /** Verficar url 404. */
    @Test
    public void teste404() {
        try {
            var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:7070/servico-inexistente")).GET().build(), HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(404, response.statusCode());
            Assertions.assertEquals("{ \"server\": \"Souza Server\", \"message\": \"Página não encontrada\" }", response.body());
        } catch (InterruptedException | IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    /** Verficar static folder. */
    @Test
    public void testeStaticFolder() {
        try {
            var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:7070/teste.html")).GET().build(), HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("<html><head><title>Teste</title></head><body>Ação</body></html>", response.body());
        } catch (InterruptedException | IOException e) {
            Assertions.fail(e.getMessage());
        }
    }
}