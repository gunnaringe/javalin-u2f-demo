package com.github.gunnaringe.javalin_u2f_demo.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.SignRequestData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SignRequestStorage {
    // TODO: Store in Redis (or memcached or whatever)
    private final Map<String, SignRequestData> requestStorage = new HashMap<>();

    public void put(String requestId, SignRequestData signRequestData) {
        requestStorage.put(requestId, signRequestData);
    }

    public SignRequestData remove(String requestId) {
        return requestStorage.remove(requestId);
    }
}
