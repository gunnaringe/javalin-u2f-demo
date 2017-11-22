package com.github.gunnaringe.javalin_u2f_demo.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yubico.u2f.data.messages.RegisterRequestData;
import com.yubico.u2f.data.messages.SignRequestData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegisterRequestStorage {
    // TODO: Store in Redis (or memcached or whatever)
    private final Map<String, RegisterRequestData> requestStorage = new HashMap<>();

    public void put(String requestId, RegisterRequestData signRequestData) {
        requestStorage.put(requestId, signRequestData);
    }

    public RegisterRequestData remove(String requestId) {
        return requestStorage.remove(requestId);
    }
}
