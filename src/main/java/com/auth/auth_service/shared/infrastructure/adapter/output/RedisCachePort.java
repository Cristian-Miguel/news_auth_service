package com.auth.auth_service.shared.infrastructure.adapter.output;

import java.util.concurrent.TimeUnit;

public interface RedisCachePort {
    void save(String key, Object value, long timeout, TimeUnit timeUnit);
    
    Object get(String key);
    
    void delete(String key);
    
    boolean hasKey(String key);
}
