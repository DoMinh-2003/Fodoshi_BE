package com.BE.service;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {
    private final ConcurrentHashMap<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    private final static long REFRESH_TOKEN_TTL = 30L;

    public RefreshTokenService() {
        // Schedule cleanup of expired tokens every hour
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    public void saveRefreshToken(String refreshToken, UUID id) {
        long expirationTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(REFRESH_TOKEN_TTL);
        tokenStore.put(refreshToken, new TokenInfo(id.toString(), expirationTime));
    }

    public UUID getIdFromRefreshToken(String refreshToken) {
        TokenInfo info = tokenStore.get(refreshToken);
        if (info != null && !isExpired(info)) {
            return UUID.fromString(info.userId);
        }
        return null;
    }

    public void deleteRefreshToken(String refreshToken) {
        tokenStore.remove(refreshToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        TokenInfo info = tokenStore.get(refreshToken);
        return info != null && !isExpired(info);
    }

    private boolean isExpired(TokenInfo info) {
        return System.currentTimeMillis() > info.expirationTime;
    }

    private void cleanupExpiredTokens() {
        tokenStore.entrySet().removeIf(entry -> isExpired(entry.getValue()));
    }

    private static class TokenInfo {
        final String userId;
        final long expirationTime;

        TokenInfo(String userId, long expirationTime) {
            this.userId = userId;
            this.expirationTime = expirationTime;
        }
    }
}
