package com.github.gunnaringe.javalin_u2f_demo.resources;

import static com.google.common.base.Preconditions.checkArgument;

import com.github.gunnaringe.javalin_u2f_demo.storage.RegisterRequestStorage;
import com.github.gunnaringe.javalin_u2f_demo.storage.UserStorage;
import com.google.common.collect.ImmutableMap;
import com.yubico.u2f.U2F;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.RegisterRequestData;
import com.yubico.u2f.data.messages.RegisterResponse;
import io.javalin.Context;
import java.security.cert.CertificateException;
import java.util.Objects;
import lombok.val;

public class RegisterResource {

    private final U2F u2f = new U2F();
    private final RegisterRequestStorage requestStorage;
    private final String appId;
    private final UserStorage userStorage;

    public RegisterResource(UserStorage userStorage, RegisterRequestStorage requestStorage, String appId) {
        this.userStorage = userStorage;
        this.requestStorage = requestStorage;
        this.appId = appId;
    }

    public void start(Context context) {
        String username = Objects.requireNonNull(context.queryParam("username"), "Username cannot be null");
        RegisterRequestData registerRequestData = u2f.startRegistration(appId, userStorage.get(username));
        requestStorage.put(registerRequestData.getRequestId(), registerRequestData);

        val model = ImmutableMap.<String, Object>builder()
                .put("data", registerRequestData.toJson())
                .put("username", username)
                .build();
        context.renderFreemarker("view/register/register.ftl", model);
    }

    public void finish(Context context) throws CertificateException, NoSuchFieldException {
        String username = Objects.requireNonNull(context.formParam("username"), "username cannot be null");
        String response = Objects.requireNonNull(context.formParam("tokenResponse"), "username cannot be null");

        RegisterResponse registerResponse = RegisterResponse.fromJson(response);
        RegisterRequestData registerRequestData = requestStorage.remove(registerResponse.getRequestId());
        DeviceRegistration registration = u2f.finishRegistration(registerRequestData, registerResponse);
        userStorage.put(username, registration);

        val model = ImmutableMap.<String, Object>builder()
                .put("registration", registration)
                .build();
        context.renderFreemarker("view/register/finished.ftl", model);
    }
}
