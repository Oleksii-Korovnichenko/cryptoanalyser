package com.javarush.cryptoanalyzer.korovnichenko.repository;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.CryptoAlphabet.*;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Alphabets {

    public static final Map<String, String> alphabets = new HashMap<>();
    private static final List<String> alphabetPriority = List.of(
            "ENG",
            "ENG_WITH_NUMBERS",
            "ENG_WITH_PUNCT",
            "ENG_WITH_NUMBERS_WITH_PUNCT",
            "UKR",
            "UKR_WITH_NUMBERS",
            "UKR_WITH_PUNCT",
            "UKR_WITH_NUMBERS_WITH_PUNCT",
            "MIXED"
    );

    static {
        register();
    }

    private static void register() {
        alphabets.put("ENG", LATIN);
        alphabets.put("ENG_WITH_NUMBERS", LATIN + NUMBERS);
        alphabets.put("ENG_WITH_PUNCT", LATIN + PUNCT);
        alphabets.put("UKR", UKR);
        alphabets.put("UKR_WITH_NUMBERS", UKR + NUMBERS);
        alphabets.put("UKR_WITH_PUNCT", UKR + PUNCT);
        alphabets.put("ENG_WITH_NUMBERS_WITH_PUNCT", LATIN + NUMBERS + PUNCT);
        alphabets.put("UKR_WITH_NUMBERS_WITH_PUNCT", UKR + NUMBERS + PUNCT);
        alphabets.put("MIXED", MIXED);
    }

    public static String getAlphabetByType(String key) {
        return alphabets.get(key.toUpperCase());
    }

    public static Map<Character, Integer> alphabetToCharIndexMap(String alphabet){
        Map<Character, Integer> charIndexMap = new HashMap<>();
        for (int i = 0; i < alphabet.length(); i++) {
            charIndexMap.put(alphabet.charAt(i), i);
        }
        return charIndexMap;
    }

    //detects alphabet by reading N lines
    public static String autodetectAlphabetType(String filePath, int linesToRead) {
        StringBuilder builder = new StringBuilder();
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null && lineCount < linesToRead) {
                builder.append(line).append(" ");
                lineCount++;
            }
        } catch (IOException e) {
            throw new ApplicationException("Error reading file " + filePath
                    + " while alphabet autodetect", e);
        }

        String text = builder.toString();
        int totalChars = text.length();
        int ukr = 0, lat = 0, digits = 0, punct = 0;

        String ukrChars = UKR;
        String latChars = LATIN;
        String punctChars = PUNCT;

        for (int i = 0; i < totalChars; i++) {
            char ch = text.charAt(i);
            if (ukrChars.indexOf(ch) >= 0) ukr++;
            else if (latChars.indexOf(ch) >= 0) lat++;
            else if (Character.isDigit(ch)) digits++;
            else if (punctChars.indexOf(ch) >= 0) punct++;
        }

        int relevant = ukr + lat + digits + punct;
        if (relevant == 0) {
            return MIXED;
        }

        double ukrRatio = ukr / (double) relevant;
        double latRatio = lat / (double) relevant;

        boolean isMixed = ukr > 0 && lat > 0 && Math.abs(ukrRatio - latRatio) < 0.15;
        if (isMixed) return MIXED;

        String base = ukrRatio > latRatio ? "UKR" : "ENG";

        return base + buildSuffix(digits > 0, punct > 0);
    }

    private static String buildSuffix(boolean hasDigits, boolean hasPunct) {
        String[] parts = new String[2];
        int count = 0;

        if (hasDigits) {
            parts[count++] = "WITH_NUMBERS";
        }
        if (hasPunct) {
            parts[count++] = "WITH_PUNCT";
        }

        if (count == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder("_");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append("_");
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    public static String findAlphabetByName(String alphabetType) {
        String resultType = null;
        for (String type : Alphabets.alphabets.keySet()) {
            if (type.equalsIgnoreCase(alphabetType)) {
                resultType = type;
            }
        }
        if (resultType != null) {
            return resultType;
        } else {
            throw new ApplicationException("Can't find alphabet in DB with name: " + alphabetType);
        }

    }
}
