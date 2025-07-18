package ru.ssyp.youtube;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

public class PasswordHasher {
    private final SecureRandom random = new SecureRandom();
    private final SecretKeyFactory factory;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public PasswordHasher() {
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] pbkdf2(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

        try {
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String hashPassword(String password) {
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return encoder.encodeToString(salt) + "$" + encoder.encodeToString(pbkdf2(password, salt));
    }

    public boolean checkPassword(String hash, String password) {
        String[] parts = hash.split(Pattern.quote("$"), 2);

        byte[] salt = decoder.decode(parts[0]);
        byte[] hashBytes = decoder.decode(parts[1]);

        return Arrays.equals(pbkdf2(password, salt), hashBytes);
    }
}
