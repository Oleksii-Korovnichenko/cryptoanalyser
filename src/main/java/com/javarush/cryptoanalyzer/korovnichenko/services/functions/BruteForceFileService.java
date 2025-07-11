package com.javarush.cryptoanalyzer.korovnichenko.services.functions;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.ConsoleUIConstants.DEFAULT_ALGORITHM;
import static com.javarush.cryptoanalyzer.korovnichenko.constants.ConsoleUIConstants.DEFAULT_ALPHABET;
import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.ERROR;
import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.SUCCESS;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.model.Cipher;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;
import com.javarush.cryptoanalyzer.korovnichenko.repository.EncryptionAlgorithm;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.CaesarCipher;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.StatisticalAnalyzer;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.VigenereCipher;
import com.javarush.cryptoanalyzer.korovnichenko.services.io.FileService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BruteForceFileService implements Function {
    private static final int ENCRYPTED_FILE_INDEX = 1;
    private static final int ANALYSIS_FILE_INDEX = 2;
    private static final int ALPHABET_INDEX = 4;
    private static final int ALGORITHM_INDEX = 3;
    private static final int LINES_TO_ANALYSE_ALPHABET = 100;
    private static final String DECRYPTED = "[DECRYPTED]";
    public static final String IO_ERROR_MESSAGE = "Can't process file:";
    public static final String BRUTE_ERROR_MESSAGE = "Brute force finished with error: ";

    private final StatisticalAnalyzer statisticalAnalyzer = new StatisticalAnalyzer();
    private final CaesarCipher caesarCipher = new CaesarCipher();

    private Cipher cipher;
    private String fileInputPath;
    private String fileOutputPath;
    private String fileForStatAnalysePath;

    @Override
    public Result execute(String[] parameters) {
        try {
            fileInputPath = parameters[ENCRYPTED_FILE_INDEX];
            fileForStatAnalysePath = parameters[ANALYSIS_FILE_INDEX];
            fileOutputPath = insertTagToPath(fileInputPath,DECRYPTED);

            // Reading files
            String encrypted = Files.readString(Paths.get(fileInputPath), StandardCharsets.UTF_8);
            String analysis = Files.readString(Paths.get(fileForStatAnalysePath), StandardCharsets.UTF_8);

            //autodetect alphabet by analysing N first lines in file or autodetect
            String alphabetParam = parameters.length > 4 ? parameters[ALPHABET_INDEX] : DEFAULT_ALPHABET;
            String detectedAlphabetFromDB = DEFAULT_ALPHABET.equalsIgnoreCase(alphabetParam)
                    ? Alphabets.autodetectAlphabetType(fileInputPath, LINES_TO_ANALYSE_ALPHABET)
                    : Alphabets.findAlphabetByName(alphabetParam);

            String algorithm = parameters.length > 3 ? parameters[ALGORITHM_INDEX] : DEFAULT_ALGORITHM;

            //int keyLength = parameters.length >= 6 ? Integer.parseInt(parameters[BRUTE_KEY_INDEX]) : 0;
            cipher = new Cipher();
            cipher.setFileInputPath(fileInputPath);
            String bestKey;
            String fileOutputPathWithKey;
            String alphabet = Alphabets.getAlphabetByType(detectedAlphabetFromDB);
            boolean isCaesar = true;
            System.out.println("Building and comparing statistical maps. Please wait....");
            Map<Character, Double> referenceFreq = statisticalAnalyzer.getFrequencies(analysis, alphabet);
            if ("VIGENERE".equalsIgnoreCase(algorithm)) {
                isCaesar = false;
                cipher.setAlgorithm(EncryptionAlgorithm.VIGENERE);
                // create new frequency  map from text example file for comparing
                List<String> topVigenereKeys = statisticalAnalyzer.findTopVigenereKeys(encrypted, referenceFreq,
                                                        detectedAlphabetFromDB, 2, 100);
                statisticalAnalyzer.writeStatisticsToFile(topVigenereKeys,"bruteforce_best_keys.txt",
                                                            encrypted, detectedAlphabetFromDB);
                bestKey = topVigenereKeys.get(0);
                if (bestKey == null || bestKey.isBlank()) {
                    return new Result(ERROR, cipher, new ApplicationException("Can't found the Vigenere key"));
                }
            } else {
                cipher.setAlgorithm(EncryptionAlgorithm.CAESAR);
                //finding the best key with statistical frequency analyzer
                int bestCaesarKey = statisticalAnalyzer.findBestCaesarKey(encrypted, detectedAlphabetFromDB, referenceFreq);
                bestKey = String.valueOf(bestCaesarKey);
            }
            // save decrypting text to file
            fileOutputPathWithKey = insertTagToPath(fileOutputPath,"_key=" + bestKey);
            cipher.setFileOutputPath(fileOutputPathWithKey);
            saveFile(fileOutputPathWithKey, isCaesar, bestKey,detectedAlphabetFromDB);
            // return result
            cipher.setAlphabetType(detectedAlphabetFromDB);
            cipher.setKey(bestKey);

            return new Result(SUCCESS, cipher);
        } catch (IOException e) {
            return new Result(ERROR, cipher, new ApplicationException(IO_ERROR_MESSAGE + e.getMessage()));
        } catch (Exception e) {
            return new Result(ERROR, cipher, new ApplicationException(BRUTE_ERROR_MESSAGE + e.getMessage()));
        }
    }

    //Insert to the end of file key tag
    private String insertTagToPath(String filename, String tag) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return filename + tag;
        }
        return filename.substring(0, dotIndex) + tag + filename.substring(dotIndex);
    }

    private void saveFile(String outputPath, boolean useCaesarCipher, String key,
                            String alphabetType) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fileInputPath), StandardCharsets.UTF_8);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.UTF_8))
        {
            for (String line : lines) {
                String processed;
                if (useCaesarCipher) {
                    processed = new CaesarCipher().decrypt(line, key, alphabetType);
                } else {
                    processed = new VigenereCipher().decrypt(line, key, alphabetType);
                }
                writer.write(processed);
                writer.newLine();
            }
        }
    }
}
