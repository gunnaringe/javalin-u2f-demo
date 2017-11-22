package com.github.gunnaringe.javalin_u2f_demo;

import com.github.gunnaringe.javalin_u2f_demo.resources.AuthResource;
import com.github.gunnaringe.javalin_u2f_demo.resources.RegisterResource;
import com.github.gunnaringe.javalin_u2f_demo.storage.RegisterRequestStorage;
import com.github.gunnaringe.javalin_u2f_demo.storage.SignRequestStorage;
import com.github.gunnaringe.javalin_u2f_demo.storage.UserStorage;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

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
                .port(8080)
                .routes(() -> {

                    path("health", () -> {
                        get(context -> context.json("Healthy"));
                    });

                    path("u2f", () -> {
                        path("/register", () -> {
                            get(registerResourceResource::start);
                            post(registerResourceResource::post);
                        });
                        path("/auth", () -> {
                            get(authResourceResource::start);
                            post(authResourceResource::post);
                        });
                    });
                })
                .enableStaticFiles("/static")
                .start();
        log.info("Started on port: {}", app.port());
    }
}
