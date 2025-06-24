package dev.shendriks.fitnesstrackerapi.supportive.apikey;

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.util.HexFormat;

@Service
public class SecureRandomApiKeyGenerator implements ApiKeyGenerator {
    @Override
    public String generateApiKey() {
        byte[] key = KeyGenerators.secureRandom(64).generateKey();
        return HexFormat.of().formatHex(key);
    }
}
