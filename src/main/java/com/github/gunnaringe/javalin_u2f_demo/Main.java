package com.github.gunnaringe.javalin_u2f_demo;

import com.github.gunnaringe.javalin_u2f_demo.resources.AuthResource;
import com.github.gunnaringe.javalin_u2f_demo.resources.RegisterResource;
import com.github.gunnaringe.javalin_u2f_demo.storage.RegisterRequestStorage;
import com.github.gunnaringe.javalin_u2f_demo.storage.SignRequestStorage;
import com.github.gunnaringe.javalin_u2f_demo.storage.UserStorage;
import io.javalin.Javalin;
import io.javalin.embeddedserver.EmbeddedServer;
import io.javalin.embeddedserver.jetty.EmbeddedJettyFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import static io.javalin.ApiBuilder.get;
import static io.javalin.ApiBuilder.path;
import static io.javalin.ApiBuilder.post;

@Slf4j
public class Main {

    public static final String APP_ID = "https://localhost:8443";

    public static void main(final String[] args) {
        final UserStorage userStorage = new UserStorage();
        final SignRequestStorage signRequestStorage = new SignRequestStorage();
        final RegisterRequestStorage registerRequestStorage = new RegisterRequestStorage();

        final AuthResource authResourceResource = new AuthResource(userStorage, signRequestStorage, APP_ID);
        final RegisterResource registerResourceResource = new RegisterResource(userStorage, registerRequestStorage, APP_ID);

        val app = Javalin.create()
                // For http instead of https; replace .embeddedServer() with `port(8080)`
                .embeddedServer(new EmbeddedJettyFactory(() -> {
                    Server server = new Server();
                    ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
                    sslConnector.setPort(8443);
                    server.setConnectors(new Connector[]{sslConnector});
                    return server;
                }))
                .routes(() -> {
                    path("health", () ->
                            get(context -> context.json("Healthy")));

                    path("u2f", () -> {
                        path("/register", () -> {
                            get(registerResourceResource::start);
                            post(registerResourceResource::finish);
                        });
                        path("/auth", () -> {
                            get(authResourceResource::start);
                            post(authResourceResource::finish);
                        });
                    });
                })
                .enableStaticFiles("/static")
                .start();
        log.info("Started on port: {}", app.port());
    }

    private static SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(EmbeddedServer.class.getResource("/keystore.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("changeit");
        return sslContextFactory;
    }
}
