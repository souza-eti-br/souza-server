package br.eti.souza.server;


import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.function.Function;
import java.util.logging.Logger;

public class Main {

    public static void main(String... args) throws IOException {
        var provider = HttpServerProvider.provider();
        var server = provider.createHttpServer(new InetSocketAddress(9999), 64);
        var context = server.createContext("/hello");
        context.getFilters().add(new TracingFilter());
        context.setHandler(respondWith(req -> HttpResponse.ok("Hello " + Instant.now()).text()));
        server.start();
    }

    static HttpHandler respondWith(HttpFunc hf) {
        return exchange -> {

            var req = HttpRequest.of(exchange);
            var res = hf.apply(req);

            var bytes = res.body().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().putAll(res.headers());

            try {
                exchange.sendResponseHeaders(res.status(), bytes.length);
                try (var os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    interface HttpFunc extends Function<HttpRequest, HttpResponse> {
    }

    static class TracingFilter extends Filter {

        private final Logger LOG = Logger.getLogger(TracingFilter.class.getName());

        @Override
        public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
            var req = HttpRequest.of(exchange);
            LOG.info(req.toString());
            chain.doFilter(exchange);
        }

        @Override
        public String description() {
            return "Trace";
        }
    }

    record HttpRequest(String method, URI requestUri, Headers headers, HttpExchange exchange) {

        static HttpRequest of(HttpExchange exchange) {
            return new HttpRequest(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI(),
                    exchange.getRequestHeaders(),
                    exchange
            );
        }
    }

    record HttpResponse(int status, Headers headers, String body) {

        static HttpResponse ok() {
            return ok("");
        }

        static HttpResponse ok(String body) {
            return new HttpResponse(200, new Headers(), body);
        }

        HttpResponse header(String name, String value) {
            var res = new HttpResponse(status(), headers(), body());
            res.headers().add(name, value);
            return res;
        }

        HttpResponse json() {
            return header("Content-type", "application/json");
        }

        HttpResponse text() {
            return header("Content-type", "text/plain");
        }
    }
}