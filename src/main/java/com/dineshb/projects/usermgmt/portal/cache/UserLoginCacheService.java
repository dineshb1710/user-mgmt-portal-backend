package com.dineshb.projects.usermgmt.portal.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserLoginCacheService {

    public static final int MAX_ATTEMPTS_ALLOWED_TO_USER = 3;

    private LoadingCache<String, Integer> userLoginCache;

    public UserLoginCacheService() {
        initCache();
    }

    private void initCache() {
        userLoginCache = CacheBuilder.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build(getCacheLoader());
    }

    private CacheLoader<String, Integer> getCacheLoader() {
        CacheLoader<String, Integer> cacheLoader = new CacheLoader<>() {
            @Override
            public Integer load(String key) {
                return 0;
            }
        };
        return cacheLoader;
    }

    public void addInvalidLoginAttemptToCache(String username) {
        userLoginCache.put(username, getAndUpdateAttempts(username));
    }

    private Integer getAndUpdateAttempts(String username) {
        return getUserAttemptsFromCache(username) + 1;
    }

    public void invalidateUserFromCache(String username) {
        userLoginCache.invalidate(username);
    }

    public boolean hasExceededMaxAttempts(String username) {
        return getUserAttemptsFromCache(username) >= MAX_ATTEMPTS_ALLOWED_TO_USER;
    }

    private int getUserAttemptsFromCache(String username) {
        try {
            return userLoginCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
