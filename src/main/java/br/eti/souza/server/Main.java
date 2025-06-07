package br.eti.souza.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {
    
    public static void main(String... args) throws Exception {
        var server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/applications/myapp", (HttpExchange t) -> {
            var response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            try (var os = t.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.start();
   }
}
