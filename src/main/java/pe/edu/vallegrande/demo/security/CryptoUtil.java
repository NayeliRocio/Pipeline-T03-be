package pe.edu.vallegrande.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CryptoUtil {

    private final SecretKeySpec secretKey;
    private final byte[] deterministicIv = new byte[16]; // Static IV for deterministic encryption

    public CryptoUtil(@Value("${app.security.encryption-key:DefaultSecretKey1234567890123456}") String keyStr) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            this.secretKey = new SecretKeySpec(key, "AES");
            // Setup a deterministic IV based on the key to ensure the same input always yields the same output
            System.arraycopy(key, 0, deterministicIv, 0, 16);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing CryptoUtil", e);
        }
    }

    // Deterministic encryption for username (so we can query it)
    public String encryptDeterministic(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, deterministicIv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println("Error while encrypting deterministically: " + e.toString());
        }
        return null;
    }

    // Deterministic decryption
    public String decryptDeterministic(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, deterministicIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Error while decrypting deterministically: " + e.toString());
        }
        return null;
    }
}
