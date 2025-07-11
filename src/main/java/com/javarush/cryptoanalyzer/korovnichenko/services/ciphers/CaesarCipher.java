package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;

public class CaesarCipher implements CipherStrategy {
    public static final String KEY_ERROR_MESSAGE = "Key for Caesar cipher must be integer number";
    @Override
    public String encrypt(String input, String key, String alphabetType) {
        int shift = 0;
        try {
            shift = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            throw new ApplicationException(KEY_ERROR_MESSAGE, e);
        }
        return shiftText(input, shift, alphabetType);
    }

    @Override
    public String decrypt(String input, String key, String alphabetType) {
        int shift = 0;
        try {
            shift = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            throw new ApplicationException(KEY_ERROR_MESSAGE, e);
        }
        return shiftText(input, -shift, alphabetType);
    }

    public String shiftText(String input, int shift, String alphabetType) {
        StringBuilder result = new StringBuilder();
        String detectedAlphabet = Alphabets.getAlphabetByType(alphabetType.toUpperCase());
        int alphabetLength = detectedAlphabet.length();
        // normalize shift
        shift = shift % alphabetLength;
        if (shift < 0) {
            shift += alphabetLength;
        }

        for (char c : input.toCharArray()) {
            int index = detectedAlphabet.indexOf(String.valueOf(c));
            if (index == -1) {
                result.append(c);
            } else {
                int newIndex = (index + shift) % alphabetLength;
                if (newIndex < 0) newIndex += alphabetLength;
                char shifted = detectedAlphabet.charAt(newIndex);
                result.append(shifted);
            }
        }
        return result.toString();
    }

}
