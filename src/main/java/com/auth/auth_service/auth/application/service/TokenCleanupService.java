package com.auth.auth_service.auth.application.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.auth.auth_service.auth.application.port.output.TokenSessionOutputPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {
     private final TokenSessionOutputPort tokenSessionOutputPort;

    // CRON EXPRESSION: Se ejecuta todos los días a las 3:00 AM
    // Formato: segundo minuto hora dia mes dia-semana
    @Scheduled(cron = "0 0 3 * * *") 
    @Transactional
    public void removeExpiredTokens() {
        log.info("🧹 Starting cleanup of expired tokens...");
        
        LocalDateTime now = LocalDateTime.now();
        tokenSessionOutputPort.deleteByExpiredAtBefore(now);
        
        log.info("✨ Cleanup finished. Expired tokens removed.");
    }
}
