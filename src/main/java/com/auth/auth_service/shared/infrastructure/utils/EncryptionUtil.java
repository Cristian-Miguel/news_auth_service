package com.auth.auth_service.shared.infrastructure.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    @Value("${crypto.secret-key}")
    private String SECRET_KEY;

    @Value("${crypto.salt}")
    private String SALT;

    // private static final String SALT = KeyGenerators.string().generateKey();

    public String encryptRefreshToken(String refreshToken){
        TextEncryptor encryptor = Encryptors.text(SECRET_KEY, SALT);
        return encryptor.encrypt(refreshToken);
    }

    public String decryptRefreshToken(String encryptedRefreshToken){
        TextEncryptor decryptor = Encryptors.text(SECRET_KEY, SALT);
        return decryptor.decrypt(encryptedRefreshToken);
    }

    public boolean verifyRefreshToken(String rawToken, String encryptedToken) {
        try {
            String decryptedDbToken = decryptRefreshToken(encryptedToken);
            return decryptedDbToken.equals(rawToken);
        } catch (Exception e) {
            return false;
        }
    }

}
