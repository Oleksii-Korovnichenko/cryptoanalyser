package com.javarush.cryptoanalyzer.korovnichenko.constants;

public class ConsoleUIConstants {
    public static final String LAUNCH = "Launching in Console UI mode...";
   /* public static final String INTRO_MESSAGE = """
                    Enter parameters separated by space  
                    [command: encrypt/decrypt] filePath key
                    (e.g. encrypt input.txt 3) 
                    (e.g. decrypt input.txt 3)
                    filePath: absolute filepath name;
                    key: can be Integer for Caesar or String for Vyginer cipher
                    by default we use Caesar cipher if you want change it
                    use the command like this : encrypt input.txt 3 caesar
                    also we can mark alphabet type manually
                    use the command like this : encrypt input.txt 3 caesar UKR_WITH_NUMBERS_PUNCT
                    we have 5 types of alphabet : 
                    "ENG", "UKR", "ENG_WITH_NUMBERS_PUNCT", "UKR_WITH_NUMBERS_PUNCT", "MIXED"
                    for brute_force command
                    brute_force filePath filePathForStaticAnalysis
                    (e.g. brute_force input.txt fileForStaticAnalysis) 
                    """;
                    */
    public static final String WELCOME = "Welcome to the Cryptographic Analyzer!";
    public static final String USAGE = "Usage: encrypt/decrypt input.txt 5 caesar [optional: ALPHABET_TYPE]"
            + " or brute_force input[ENCRYPTED].txt fileForStatAnalyse [optional: ALPHABET_TYPE]";
    public static final String PROMPT_COMMAND = "Enter your command -> ";
    public static final String PROMPT_MODE = "Mode (encrypt/decrypt/brute_force)";
    public static final String PROMPT_FILE = "File name";
    public static final String PROMPT_KEY = "Key (integer)";
    public static final String PROMPT_CIPHER = "Cipher (e.g., caesar)";
    public static final String PROMPT_ANALYSIS_FILE = "File name for frequency analyse";
    public static final String OUTPUT_FILE = "Файл после обработки: ";


}
