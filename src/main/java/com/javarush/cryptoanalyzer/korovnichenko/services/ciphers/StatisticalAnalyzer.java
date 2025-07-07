package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;

import java.util.HashMap;
import java.util.Map;

public class StatisticalAnalyzer {
private final CaesarCipher caesarCipher = new CaesarCipher();

    public int findBestCaesarKey(String cipherText, String referenceText, String alphabetType) {
        String alphabet = Alphabets.getAlphabetByType(alphabetType);
        Map<Character, Double> referenceFreq = getFrequencies(referenceText, alphabet);

        int bestKey = 0;
        double bestScore = Double.MAX_VALUE;

        for (int key = 0; key < alphabet.length(); key++) {
            String decrypted = caesarCipher.shiftText(cipherText, -key, alphabetType);
            Map<Character, Double> currentFreq = getFrequencies(decrypted, alphabet);
            double score = compareFrequencies(currentFreq, referenceFreq, alphabet);
            if (score < bestScore) {
                bestScore = score;
                bestKey = key;
            }
        }

        return bestKey;
    }

    private Map<Character, Double> getFrequencies(String text, String alphabet) {
        Map<Character, Integer> counts = new HashMap<>();
        int total = 0;

        for (char c : text.toLowerCase().toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                counts.put(c, counts.getOrDefault(c, 0) + 1);
                total++;
            }
        }

        Map<Character, Double> frequencies = new HashMap<>();
        for (char c : alphabet.toCharArray()) {
            frequencies.put(c, counts.getOrDefault(c, 0) / (double) total);
        }

        return frequencies;
    }

    private static double compareFrequencies(Map<Character, Double> f1, Map<Character, Double> f2, String alphabet) {
        double sum = 0;
        for (char c : alphabet.toCharArray()) {
            double diff = f1.getOrDefault(c, 0.0) - f2.getOrDefault(c, 0.0);
            sum += Math.abs(diff);
        }
        return sum;
    }
}
