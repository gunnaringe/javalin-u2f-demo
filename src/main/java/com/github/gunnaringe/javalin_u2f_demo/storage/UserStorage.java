package com.github.gunnaringe.javalin_u2f_demo.storage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.yubico.u2f.data.DeviceRegistration;
import java.util.Collection;

public class UserStorage {
    // TODO: Store in DB
    private final Multimap<String, DeviceRegistration> userStorage = ArrayListMultimap.create();

    public Collection<DeviceRegistration> get(final String username) {
        return userStorage.get(username);
    }

    public void put(final String username, final DeviceRegistration deviceRegistration) {
        userStorage.put(username, deviceRegistration);
    }
}
