package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

import com.javarush.cryptoanalyzer.korovnichenko.model.Cipher;
import com.javarush.cryptoanalyzer.korovnichenko.repository.CipherRegistry;

public class CipherServiceImpl implements CipherService {
    private final Cipher cipher;

    public CipherServiceImpl(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public String encrypt(String key) {
        cipher.setKey(key);
        return CipherRegistry
                .getStrategy(cipher.getAlgorithm())
                .encrypt(cipher.getText(), cipher.getKey(), cipher.getAlphabetType());
    }

    @Override
    public String decrypt(String key) {
        cipher.setKey(key);
        return CipherRegistry
                .getStrategy(cipher.getAlgorithm())
                .decrypt(cipher.getText(), cipher.getKey(), cipher.getAlphabetType());
    }
}
