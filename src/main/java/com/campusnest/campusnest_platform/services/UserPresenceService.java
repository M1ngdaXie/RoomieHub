package com.campusnest.campusnest_platform.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPresenceService {

    // In-memory storage for user presence (in production, use Redis)
    private final Map<Long, Boolean> onlineUsers = new ConcurrentHashMap<>();

    public void setUserOnline(Long userId) {
        onlineUsers.put(userId, true);
    }

    public void setUserOffline(Long userId) {
        onlineUsers.remove(userId);
    }

    public boolean isUserOnline(Long userId) {
        return onlineUsers.getOrDefault(userId, false);
    }

    public int getOnlineUserCount() {
        return onlineUsers.size();
    }
}
