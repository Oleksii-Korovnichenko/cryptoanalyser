package com.javarush.cryptoanalyzer.korovnichenko.ui;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.ApplicationCompletionConstants.*;
import static com.javarush.cryptoanalyzer.korovnichenko.constants.ConsoleUIConstants.*;

import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.services.io.FileService;

import java.io.File;
import java.util.Scanner;

public class ConsoleUI implements UI {
    private static final int PARAMS_COUNT = 4;
    private static final int MODE_INDEX = 0;
    private static final int INPUT_FILE_INDEX = 1;
    private static final int KEY_INDEX = 2;
    private static final int CIPHER_INDEX = 3;


    private String[] params;

    public ConsoleUI() {
    }

    public ConsoleUI(String[] parametersWithoutUI) {
        this.params = parametersWithoutUI;
    }

    @Override
    public String[] getUIParameters() {
        if (params != null && params.length >= PARAMS_COUNT) {
            String cipher = DEFAULT_ALGORITHM;
            if (params.length == PARAMS_COUNT) {
                cipher = params[CIPHER_INDEX];
            }
            //mode, file, key, cipher,
            return new String[] { params[MODE_INDEX], params[INPUT_FILE_INDEX], params[KEY_INDEX], cipher};
        }

        System.out.println(LAUNCH);
        System.out.println(WELCOME);
        System.out.println(USAGE);
        System.out.print(PROMPT_COMMAND);

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        String[] parts = input.split("\\s+");

        String mode = promptIfMissing(parts, MODE_INDEX, PROMPT_MODE, null);
        String alphabet = DEFAULT_ALPHABET;

        if (!mode.equalsIgnoreCase(BRUTE_FORCE)) {
            String file;
            if (mode.equalsIgnoreCase(ENCRYPT)) {
                file = promptIfMissing(parts, 1, PROMPT_FILE, DEFAULT_SOURCE_FILE_PATH);
            } else {
                file = promptIfMissing(parts, 1, PROMPT_FILE, DEFAULT_ENCRYPT_FILE_PATH);
            }
            String key = promptValidKey(parts, 2, PROMPT_KEY, DEFAULT_KEY);
            String cipher = promptIfMissing(parts, 3, PROMPT_CIPHER, DEFAULT_ALGORITHM);

            if (parts.length > PARAMS_COUNT && !parts[PARAMS_COUNT].isBlank()) {
                alphabet = parts[PARAMS_COUNT];
            }

            return new String[] { mode, file, key, cipher, alphabet };
        } else {
            String file = promptIfMissing(parts, INPUT_FILE_INDEX, PROMPT_FILE, DEFAULT_ENCRYPT_FILE_PATH);
            String analysisFile = promptIfMissing(parts, KEY_INDEX, PROMPT_ANALYSIS_FILE, DEFAULT_ANALYSE_FILE_PATH);
            String cipher = promptIfMissing(parts, CIPHER_INDEX, PROMPT_CIPHER, DEFAULT_ALGORITHM);
            if (parts.length > PARAMS_COUNT && !parts[PARAMS_COUNT].isBlank()) {
                alphabet = parts[PARAMS_COUNT];
            } else {
                alphabet = "auto";
            }

            return new String[] { mode, file, analysisFile, cipher, alphabet };
        }
    }
    //brute_force input[ENCRYPTED].txt static-analysis.txt caesar
    @Override
    public void printResult(Result result) {
        switch (result.getResultCode()) {
            case SUCCESS -> {
                System.out.println(SUCCESS);
                System.out.println(USED_CIPHER + result.getUsedCipher().getAlgorithm().name());
                System.out.println(USED_ALPHABET + result.getUsedCipher().getAlphabetType());
                System.out.println(USED_KEY + result.getUsedCipher().getKey());
                System.out.println(OUTPUT_FILE + result.getUsedCipher().getFileOutputPath());
                String inputPath = result.getUsedCipher().getFileInputPath();
                String outputPath = result.getUsedCipher().getFileOutputPath();
                System.out.println("First 10 lines from input file :" + inputPath);
                System.out.println(new FileService().readFirstLines(new File(inputPath), 10));
                System.out.println("First 10 lines from processed file :" + outputPath);
                System.out.println(new FileService().readFirstLines(new File(outputPath), 10));
                System.out.println("For vigerene like ciphers in brute force mode if best founded key not correct - "
                        + "see additional report with the best 100 closely keys in bruteforce_best_keys.txt");
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
                        System.out.println("Attention: " + WRONG_CAESAR_KEY_MESSAGE + key);
                    return key;
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
