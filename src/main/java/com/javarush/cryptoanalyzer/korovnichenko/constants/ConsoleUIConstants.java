package com.javarush.cryptoanalyzer.korovnichenko.constants;

public class ConsoleUIConstants {
    public static final String LAUNCH = "Launching in Console UI mode...";
    public static final String CHOOSE_THE_MODE = """
            Choose working mode: type [GUI] or default : [ConsoleUI]");
            Type 'exit' to quit the program.""";
    public static final String EXIT = "exit";
    public static final String EXIT_MESSAGE = "Exiting the program. Goodbye!";
    public static final String LAUNCH_GUI_MESSAGE = "Launching in GUI mode ...";
    public static final String GUI_LAUNCH_PROBLEM = """
            "Can't launch GUI mode:
            Run application with parameters:
            java --module-path [path for javafx-sdk\\\\lib]
            --add-modules javafx.controls,javafx.graphics
            -jar cryptoanalyzer-ver1.0.jar
            """;
    public static final String GUI_MODE = "GUI";
    public static final String WELCOME = "Welcome to the Cryptographic Analyzer!";
    public static final String USAGE = """
            =============================================================================
            For using please type commands like:
            ----------------------------------------------------------------------------- 
            \u001B[32mencrypt/decrypt input.txt 5 caesar [optional: ALPHABET_TYPE]\u001B[0m
            ------------------------------------------------------------------------------
            \u001B[32mbrute_force input[ENCRYPTED].txt fileForStatAnalyse [optional: ALPHABET_TYPE]\u001B[0m
            -----------------------------------------------------------------------------
            Alphabet types:
            \u001B[32mUKR, ENG, UKR_WITH_PUNCT, ENG_WITH_PUNCT, MIXED,
            UKR_WITH_PUNCT_WITH_NUMBERS, ENG_WITH_PUNCT_WITH_NUMBERS\u001B[0m
            ==============================================================================
            """;
    public static final String PROMPT_COMMAND = "Enter your command -> ";
    public static final String PROMPT_MODE = "Mode (encrypt/decrypt/brute_force)";
    public static final String PROMPT_FILE = "File name";
    public static final String PROMPT_KEY = "Key (integer)";
    public static final String PROMPT_CIPHER = "Cipher (e.g., caesar)";
    public static final String PROMPT_ANALYSIS_FILE = "File name for frequency analyse";
    public static final String OUTPUT_FILE = "Файл после обработки: ";
    public static final String DEFAULT_SOURCE_FILE_PATH = "input.txt";
    public static final String DEFAULT_ENCRYPT_FILE_PATH = "input[ENCRYPTED].txt";
    public static final String DEFAULT_KEY = "3";
    public static final String DEFAULT_ALGORITHM = "caesar";
    public static final String DEFAULT_ALPHABET = "auto";
    public static final String DEFAULT_ANALYSE_FILE_PATH = "static-analysis.txt";
    public static final String BRUTE_FORCE = "brute_force";
    public static final String ENCRYPT = "encrypt";




}
