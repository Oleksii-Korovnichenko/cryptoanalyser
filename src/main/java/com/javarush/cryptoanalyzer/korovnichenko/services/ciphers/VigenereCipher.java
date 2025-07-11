package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;

public class VigenereCipher implements CipherStrategy {
    public static final String KEY_ERROR_MESSAGE =
            "Key for Vigenere cipher must contain only letters from the selected alphabet";

    @Override
    public String encrypt(String input, String key, String alphabetType) {
        return processText(input, key, alphabetType, true);
    }

    @Override
    public String decrypt(String input, String key, String alphabetType) {
        return processText(input, key, alphabetType, false);
    }

    public String processText(String input, String key, String alphabetType, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        String detectedAlphabet = Alphabets.getAlphabetByType(alphabetType.toUpperCase());
        int alphabetLength = detectedAlphabet.length();

        // Validate key
        if (!key.chars().allMatch(c -> detectedAlphabet.indexOf(c) >= 0)) {
            throw new ApplicationException(KEY_ERROR_MESSAGE);
        }

        int keyIndex = 0;

        for (char c : input.toCharArray()) {

            if (c == '\n' || c == '\r') {
                result.append(c);
                continue; // Не продвигаем ключ
            }
            char keyChar = key.charAt(keyIndex % key.length());
            int keyShift = detectedAlphabet.indexOf(keyChar);

            if (keyShift == -1) {
                throw new ApplicationException("Недопустимый символ в ключе: '" + keyChar + "'");
            }

            int charIndex = detectedAlphabet.indexOf(c);


            if (charIndex == -1) {
                result.append(c); // Символ вне алфавита — не шифруем, не двигаем ключ
            } else {
                keyChar = key.charAt(keyIndex % key.length());
                keyShift = detectedAlphabet.indexOf(keyChar);

                int newIndex = encrypt
                        ? (charIndex + keyShift) % alphabetLength
                        : (charIndex - keyShift + alphabetLength) % alphabetLength;

                result.append(detectedAlphabet.charAt(newIndex));
                keyIndex++; // Двигаем ключ ТОЛЬКО после успешной шифровки
            }
        }

        return result.toString();
    }
}