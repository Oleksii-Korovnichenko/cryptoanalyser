package com.javarush.cryptoanalyzer.korovnichenko.services.functions;
import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.ERROR;
import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.SUCCESS;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.model.Cipher;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;
import com.javarush.cryptoanalyzer.korovnichenko.repository.EncryptionAlgorithm;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.CaesarCipher;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.StatisticalAnalyzer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BruteForceFileService implements Function {
    private static final int ENCRYPTED_FILE_INDEX = 1;
    private static final int ANALYSIS_FILE_INDEX = 2;
    private static final int ALPHABET_INDEX = 3;
    private static final int LINES_TO_ANALYSE_ALPHABET = 100;
    private static final String DECRYPTED = "[DECRYPTED]";

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
            String encrypted = Files.readString(Paths.get(fileInputPath));
            String analysis = Files.readString(Paths.get(fileForStatAnalysePath));

            //autodetect alphabet by analysing N first lines in file or autodetect
            String alphabetParam = parameters.length >= 4 ? parameters[ALPHABET_INDEX] : "auto";

            String detectedAlphabetFromDB = "auto".equalsIgnoreCase(alphabetParam)
                    ? Alphabets.autodetectAlphabetType(fileInputPath, LINES_TO_ANALYSE_ALPHABET)
                    : Alphabets.findAlphabetByName(alphabetParam);

            //finding the best key with statistical frequency analyzer
            int bestCaesarKey = statisticalAnalyzer.findBestCaesarKey(encrypted, analysis, detectedAlphabetFromDB);
            String bestKey = String.valueOf(bestCaesarKey);
            //decrypting with founded bestKey
            String decrypted = caesarCipher.decrypt(encrypted, bestKey, detectedAlphabetFromDB);

            // save decrypting text to file
            String fileOutputPathWithKey = insertTagToPath(fileOutputPath,"_key=" + bestKey);
            saveFile(decrypted, fileOutputPathWithKey);

            // return result
            cipher = new Cipher();
            cipher.setFileOutputPath(fileOutputPathWithKey);
            cipher.setFileInputPath(fileInputPath);
            cipher.setAlgorithm(EncryptionAlgorithm.CAESAR);
            cipher.setAlphabetType(detectedAlphabetFromDB);
            cipher.setKey(bestKey);

            return new Result(SUCCESS, cipher);

        } catch (IOException e) {
            return new Result(ERROR, cipher, new ApplicationException("Can't process file: " + e.getMessage()));
        } catch (Exception e) {
            return new Result(ERROR, cipher, new ApplicationException("Brute force finished with error: " + e.getMessage()));
        }
    }

    private void saveFile(String fullText, String path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
                writer.write(fullText);
        }
    }

    //Insert to the end of file name [DECRYPTED] tag
    private String insertTagToPath(String filename, String tag) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return filename + tag;
        }
        return filename.substring(0, dotIndex) + tag + filename.substring(dotIndex);
    }
}
