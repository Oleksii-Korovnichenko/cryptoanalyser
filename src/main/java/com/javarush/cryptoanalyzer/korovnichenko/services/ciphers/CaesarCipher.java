package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;

public class CaesarCipher implements CipherStrategy {
    @Override
    public String encrypt(String input, String key, String alphabet) {
        int shift = 0;
        try {
            shift = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            throw new ApplicationException("Key for Caesar cipher must be integer number", e);
        }
        return shiftText(input, shift, alphabet);
    }

    @Override
    public String decrypt(String input, String key, String alphabet) {
        int shift = 0;
        try {
            shift = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            throw new ApplicationException("Key for Caesar cipher must be integer number", e);
        }
        return shiftText(input, -shift, alphabet);
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
            boolean isUpper = Character.isUpperCase(c);
            char lowerC = Character.toLowerCase(c);

            int index = detectedAlphabet.indexOf(lowerC);
            if (index == -1) {
                result.append(c);
            } else {
                int newIndex = (index + shift) % alphabetLength;
                if (newIndex < 0) newIndex += alphabetLength;
                char shifted = detectedAlphabet.charAt(newIndex);
                result.append(isUpper ? Character.toUpperCase(shifted) : shifted);
            }
        }
        return result.toString();
    }

}
