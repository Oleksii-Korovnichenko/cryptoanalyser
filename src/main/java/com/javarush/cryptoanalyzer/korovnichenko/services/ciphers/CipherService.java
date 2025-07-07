package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

public interface CipherService {
    String encrypt(String input);
    String decrypt(String input);
}
