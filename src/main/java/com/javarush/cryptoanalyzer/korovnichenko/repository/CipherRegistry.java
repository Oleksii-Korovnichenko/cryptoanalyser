package com.javarush.cryptoanalyzer.korovnichenko.repository;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.CaesarCipher;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.CipherStrategy;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.VigenereCipher;

import java.util.HashMap;
import java.util.Map;

public class CipherRegistry {
    private static final Map<EncryptionAlgorithm, CipherStrategy> strategies = new HashMap<>();

    static {
        strategies.put(EncryptionAlgorithm.CAESAR, new CaesarCipher());
        strategies.put(EncryptionAlgorithm.VIGENERE, new VigenereCipher());
    }

    public static CipherStrategy getStrategy(EncryptionAlgorithm algorithm) {
        CipherStrategy strategy = strategies.get(algorithm);
        if (strategy == null) {
            throw new ApplicationException("Unknown cipher: " + algorithm);
        }
        return strategy;
    }

    public static EncryptionAlgorithm findAlgorithmByName(String algorithm) {
        for (EncryptionAlgorithm type : EncryptionAlgorithm.values()) {
            if (type.name().equalsIgnoreCase(algorithm)) {
                return type;
            }
        }
        return EncryptionAlgorithm.CAESAR;
    }
}
