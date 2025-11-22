package com.example.onharu.global.jwt;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LogoutTokenStore {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, Instant expiresAt) {
        cleanup();
        revokedTokens.put(token, expiresAt);
    }

    public boolean isRevoked(String token) {
        cleanup();
        Instant expiry = revokedTokens.get(token);
        if (expiry == null) {
            return false;
        }
        if (expiry.isBefore(Instant.now())) {
            revokedTokens.remove(token);
            return false;
        }
        return true;
    }

    private void cleanup() {
        Instant now = Instant.now();
        Iterator<Map.Entry<String, Instant>> iterator = revokedTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Instant> entry = iterator.next();
            if (entry.getValue().isBefore(now)) {
                iterator.remove();
            }
        }
    }
}
