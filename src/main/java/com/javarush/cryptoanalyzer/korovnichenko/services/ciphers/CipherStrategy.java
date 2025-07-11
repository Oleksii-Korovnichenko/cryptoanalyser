package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

public interface CipherStrategy {
    String encrypt(String input, String key, String alphabetType);

    String decrypt(String input, String key, String alphabetType);
}
