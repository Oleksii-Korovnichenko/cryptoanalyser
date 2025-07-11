package com.javarush.cryptoanalyzer.korovnichenko.services.ciphers;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticalAnalyzer {
    private List<String> regexStrings = List.of(
                    "\\b(і|та|в|на|до|що|як|це|але|чи|з|у|й|із|при)\\b", // служебные слова
                    "\\b[А-ЯІЇЄҐ][а-яіїєґ]{3,}\\b",                      // заглавные слова длиной ≥4
                    "\\b[а-яіїєґ]{6,}\\b",                               // длинные слова (≥6 символов)
                    "[а-яіїєґ]+\\s+[а-яіїєґ]+",                          // два украинских слова через пробел
                    "\\b\\w+?(ськ|зьк|ський|зький)(?=\\s|\\p{Punct})",    // характерные суффиксы + пробел
                    "\\b\\d{2,4}\\b",                                    // числа: год, день, размер
                    "[А-ЯІЇЄҐ][а-яіїєґ]+[,\\.\\s]",                      // заглавное слово + пунктуация
                    "\\b\\w+(ення|ування|ист|изм|ик|ар|тель)\\b",         // отыменные суффиксы
                    "\\bне\\s+\\w{4,}\\b",                               // конструкция "не + слово"
                    "([а-яіїєґ]{4,})(\\p{Punct}|\\s){1,2}\\1"            // повторы слов (ритм, поэзия)
            );


    private final CaesarCipher caesarCipher = new CaesarCipher();

    public int findBestCaesarKey(String cipherText, String alphabetType, Map<Character, Double> referenceFreq ) {
        String alphabet = Alphabets.getAlphabetByType(alphabetType);

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

    public Map<Character, Double> getFrequencies(String text, String alphabet) {
        Map<Character, Integer> counts = new HashMap<>();
        int total = 0;

        for (char c : text.toCharArray()) {
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

    public List<String> findTopVigenereKeys(String cipherText, Map<Character, Double> referenceFreq,
                                  String alphabetType, int keyLength, int topN) {
        String alphabet = Alphabets.getAlphabetByType(alphabetType);
        if (alphabet == null) {
            throw new IllegalArgumentException("Unknown alphabet type: " + alphabetType);
        }

        // optimization by time processing for cipher text take only first 1000 symbols
        if (cipherText.length() > 500) {
            cipherText = cipherText.substring(0, 1000);
        }

        VigenereCipher cipher = new VigenereCipher();
        Map<String, Double> topKeys = new LinkedHashMap<>();

        int alphabetLen = alphabet.length();
        int totalKeys = (int) Math.pow(alphabetLen, keyLength);
        System.out.println("Processing all " + totalKeys + " keys. Please wait....");
        int possibleKeyCount = 0;
        // processing all keys with keyLength
        for (int index = 0; index < totalKeys; index++) {
            StringBuilder keyBuilder = new StringBuilder();

            int temp = index;
            for (int i = 0; i < keyLength; i++) {
                keyBuilder.insert(0, alphabet.charAt(temp % alphabetLen));
                temp /= alphabetLen;
            }

            String key = keyBuilder.toString();
            String decrypted = cipher.decrypt(cipherText, key, alphabetType);

            // 🔍 Hard checking for patterns matching
            int matchCount = 0;

            for (String regex : regexStrings) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(decrypted);

                if (matcher.find()) {
                    matchCount++;
                }
            }

            // here we can do hard check complicity
            if (matchCount < 5) {
                continue; // do not check if not match our patterns
            }
            possibleKeyCount++;
           // System.out.println("✅ Key " +key+ " patterns all right " + matchCount);

            Map<Character, Double> freq = getFrequencies(decrypted, alphabet);
            //double score = compareFrequencies(freq, referenceFreq, alphabet); // another type of scoring
            double score = compareFrequenciesCosine(freq, referenceFreq, alphabet);
            //score += calculatePenalty(decrypted); // for addition criteria checks
            updateTopKeySet(key, score, topKeys, topN);
        }
        System.out.println("Founded best " + possibleKeyCount + " to proceed...");
        // Sorting Map by score value ↑
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(topKeys.entrySet());
        sorted.sort(Comparator.comparingDouble(Map.Entry::getValue));

        // take only N keys
        List<String> topNKeys = new ArrayList<>();

        for (int i = 0; i < Math.min(20, sorted.size()); i++) {
            String key = sorted.get(i).getKey();
            topNKeys.add(key);
        }

        ArrayList<Map.Entry<String, Double>> sortedList = new ArrayList<>(topKeys.entrySet());
        sorted.sort(Comparator.comparingDouble(Map.Entry::getValue));

        // Take only topN keys
        List<String> topKeysArray = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, sortedList.size()); i++) {
            String key = sorted.get(i).getKey();
            topKeysArray.add(key);
        }

        return topKeysArray;
    }

    private void updateTopKeySet(String key, double score, Map<String, Double> topKeys, int maxSize) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("log.txt", true))) {
            if (topKeys.size() < maxSize) {
                topKeys.put(key, score);
                logWriter.write("✅ Add: " + key + " → " + score + "\n");
            } else {
                String worstKey = null;
                double worstScore = -1.0;

                for (Map.Entry<String, Double> entry : topKeys.entrySet()) {
                    if (entry.getValue() > worstScore) {
                        worstScore = entry.getValue();
                        worstKey = entry.getKey();
                    }
                }

                if (score < worstScore && worstKey != null) {
                    topKeys.remove(worstKey);
                    topKeys.put(key, score);
                    logWriter.write("🔁 CHANGING: " + worstKey + " (" + worstScore + ") with " + key + " (" + score + ")\n");
                } else {
                    logWriter.write("🚫 FALSE: " + key + " → " + score + " (worst)\n");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Log Error: " + e.getMessage());
        }
    }

    public void writeStatisticsToFile(List<String> sortedKeys, String outputPath, String cipherText,
                                                                                        String alphabetType) {
        VigenereCipher cipher = new VigenereCipher();
        File outputFile = new File(outputPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(String.format("%-10s %-60s%n", "KEY", "DECRYPTED TEXT (preview)"));
            writer.write("--------------------------------------------------------------\n");

            for (String key : sortedKeys) {
                String decrypted = cipher.decrypt(cipherText, key, alphabetType);

                String preview = decrypted.replaceAll("\\s+", " ").trim();
                if (preview.length() > 100) {
                    preview = preview.substring(0, 100) + "...";
                }

                writer.write(String.format("%-10s %-60s%n", key, preview));
                writer.write("\n--------------------------------------------------------------\n");
            }

            writer.flush();
            System.out.println("📄 The results was written to: " + outputFile.getAbsolutePath());

        } catch (IOException e) {
            throw new ApplicationException("Error best key writing to file " + e.getMessage());
        }
    }

    public double compareFrequenciesCosine(Map<Character, Double> freqA, Map<Character, Double> freqB,
                                           String alphabet) {
        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (char c : alphabet.toCharArray()) {
            double a = freqA.getOrDefault(c, 0.0);
            double b = freqB.getOrDefault(c, 0.0);

            dotProduct += a * b;
            magnitudeA += a * a;
            magnitudeB += b * b;
        }

        // no dividing on zero
        if (magnitudeA == 0 || magnitudeB == 0) {
            return 1.0; // Maximum distance
        }

        double cosineSimilarity = dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));

        // Чем выше cosineSimilarity, тем лучше — но для совместимости с "score"
        return 1.0 - cosineSimilarity; // интерпретируем как "расстояние"
    }

    /*
    public double calculatePenalty(String decryptedText) {
        double penalty = 0.0;

        int spaceCount = decryptedText.length() - decryptedText.replace(" ", "").length();
        if (spaceCount < 3) {
            penalty += 0.15; // calculate spaces in text
        }

        // remove all spaces
        String cleaned = decryptedText.replaceAll("\\s+", " ").trim();

        // patterns
        int matchCount = 0;
        List<String> regexStrings = List.of(
                "\\b(і|та|не|в|на|до|що|як|це|бо|але)\\b",            // служебные слова
                "[А-ЯІЇЄҐ][а-яіїєґ]{2,}",                             // заглавные слова
                "\\b[а-яіїєґ]{4,}\\b",                                // длинные слова
                "\\b\\d{2,4}\\b",                                     // цифры (годы и пр.)
                ".*[а-яіїєґ]+\\s+[а-яіїєґ]+.*"                        // два слова через пробел
        );

        for (String regex : regexStrings) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cleaned);
            if (matcher.find()) {
                matchCount++;
            }
        }

        if (matchCount < 2) {
            penalty += 0.7; // мало признаков осмысленного текста
        }

        return penalty; // можно добавить лог: System.out.println("Penalty: " + penalty);
    }
    */
}
