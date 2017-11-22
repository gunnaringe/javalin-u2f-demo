package com.github.gunnaringe.javalin_u2f_demo.resources;

import com.github.gunnaringe.javalin_u2f_demo.storage.SignRequestStorage;
import com.github.gunnaringe.javalin_u2f_demo.storage.UserStorage;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.yubico.u2f.U2F;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.SignRequestData;
import com.yubico.u2f.data.messages.SignResponse;
import com.yubico.u2f.exceptions.DeviceCompromisedException;
import com.yubico.u2f.exceptions.NoEligibleDevicesException;
import io.javalin.Context;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class AuthResource {

    private final U2F u2f = new U2F();
    private SignRequestStorage requestStorage;
    private final String appId;
    private UserStorage userStorage;

    public AuthResource(UserStorage userStorage, SignRequestStorage requestStorage, String appId) {
        this.userStorage = userStorage;
        this.requestStorage = requestStorage;
        this.appId = appId;
    }

    public void start(Context context) {
        String username = Strings.nullToEmpty(context.queryParam("username"));
        try {
            SignRequestData signRequestData = u2f.startSignature(appId, getRegistrations(username));
            requestStorage.put(signRequestData.getRequestId(), signRequestData);
            final Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("username", username)
                    .put("data", signRequestData)
                    .build();
            context.renderFreemarker("view/auth/authenticate.ftl", model);
        } catch (NoEligibleDevicesException e) {
            final Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("username", username)
                    .put("data", new SignRequestData(appId, "", Collections.emptyList()))
                    .build();
            context.renderFreemarker("view/auth/authenticate.ftl", model);
        }
    }

    public void post(Context context) {
        String username = context.formParam("username");
        SignResponse signResponse = SignResponse.fromJson(Objects.requireNonNull(context.formParam("tokenResponse"), "tokenResponse cannot be null"));
        SignRequestData authenticateRequest = requestStorage.remove(signResponse.getRequestId());
        DeviceRegistration registration = null;
        try {
            registration = u2f.finishSignature(authenticateRequest, signResponse, getRegistrations(username));
            final Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("success", true)
                    .put("messages", ImmutableList.of())
                    .build();
            context.renderFreemarker("view/auth/finished.ftl", model);

        } catch (DeviceCompromisedException e) {
            registration = e.getDeviceRegistration();
            final Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("success", false)
                    .put("messages", ImmutableList.of("Device possibly compromised and therefore blocked: " + e.getMessage()))
                    .build();
            context.renderFreemarker("view/auth/finished.ftl", model);
        } finally {
            userStorage.put(username, registration);
        }
    }

    private Iterable<DeviceRegistration> getRegistrations(String username) {
        return userStorage.get(username);
    }
}
