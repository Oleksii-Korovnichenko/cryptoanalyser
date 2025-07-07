package com.javarush.cryptoanalyzer.korovnichenko.ui;

import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import java.util.Scanner;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.ApplicationCompletionConstants.*;
import static com.javarush.cryptoanalyzer.korovnichenko.constants.ConsoleUIConstants.*;

public class ConsoleUI implements UI {
    private String[] params;

    public ConsoleUI() {
    }

    public ConsoleUI(String[] parametersWithoutUI) {
        this.params = parametersWithoutUI;
    }

    @Override
    public String[] getUIParameters() {
        if (params != null && params.length >=4) {
            String cipher = "CAESAR";
            if (params.length == 4) {
                cipher = params[3];
            }
            //mode, file, key, cipher,
            return new String[] { params[0], params[1], params[2], cipher};
        }

        System.out.println(LAUNCH);
        System.out.println(WELCOME);
        System.out.println(USAGE);
        System.out.print(PROMPT_COMMAND);

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        String[] parts = input.split("\\s+");

        String mode = promptIfMissing(parts, 0, PROMPT_MODE, null);
        String alphabet = "auto";

        if (!mode.equalsIgnoreCase("brute_force")) {
            String file = "";
            if (mode.equalsIgnoreCase("encrypt")) {
                file = promptIfMissing(parts, 1, PROMPT_FILE, "input.txt");
            } else {
                file = promptIfMissing(parts, 1, PROMPT_FILE, "input[ENCRYPTED].txt");
            }
            String key = promptValidKey(parts, 2, PROMPT_KEY, "3");
            String cipher = promptIfMissing(parts, 3, PROMPT_CIPHER, "caesar");

            if (parts.length > 4 && !parts[4].isBlank()) {
                alphabet = parts[4];
            }

            return new String[] { mode, file, key, cipher, alphabet };
        } else {
            String file = promptIfMissing(parts, 1, PROMPT_FILE, "input[ENCRYPTED].txt");
            String analysisFile = promptIfMissing(parts, 2, PROMPT_ANALYSIS_FILE, "static-analysis.txt");

            if (parts.length > 3 && !parts[3].isBlank()) {
                alphabet = parts[3];
            }

            return new String[] { mode, file, analysisFile, alphabet };
        }
    }

    @Override
    public void printResult(Result result) {
        switch (result.getResultCode()) {
            case SUCCESS -> {
                System.out.println(SUCCESS);
                System.out.println(USED_CIPHER + result.getUsedCipher().getAlgorithm().name());
                System.out.println(USED_ALPHABET + result.getUsedCipher().getAlphabetType());
                System.out.println(USED_KEY + result.getUsedCipher().getKey());
                System.out.println(OUTPUT_FILE + result.getUsedCipher().getFileOutputPath());
            }
            case ERROR -> {
                System.out.println(EXCEPTION);
                System.out.println(result.getApplicationException().getMessage());
            }
        }
    }

    private String promptIfMissing(String[] input, int index, String prompt, String defaultValue) {
        Scanner scanner = new Scanner(System.in);

        if (input.length > index && !input[index].isBlank()) {
            return input[index];
        }

        String result;
        do {
            System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]" : "") + ": ");
            result = scanner.nextLine().trim();
            if (result.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
        } while (result.isEmpty());

        return result;
    }

    private String promptValidKey(String[] input, int index, String prompt, String defaultValue) {
        Scanner scanner = new Scanner(System.in);
        String key = (input.length > index && !input[index].isBlank()) ? input[index] : "";

        while (true) {
            if (!key.isEmpty()) {
                try {
                    Integer.parseInt(key);
                    return key;
                } catch (NumberFormatException e) {
                    System.out.println("Key fo Caesar cipher must be integer value: " + key);
                }
            }

            System.out.print(prompt + (defaultValue != null ? " [" + defaultValue + "]" : "") + ": ");
            key = scanner.nextLine().trim();
            if (key.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
        }
    }
}
