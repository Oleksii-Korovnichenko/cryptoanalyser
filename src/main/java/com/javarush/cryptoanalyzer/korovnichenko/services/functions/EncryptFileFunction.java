package com.javarush.cryptoanalyzer.korovnichenko.services.functions;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.ConsoleUIConstants.DEFAULT_ALPHABET;
import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.ERROR;
import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.SUCCESS;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.model.Cipher;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.repository.Alphabets;
import com.javarush.cryptoanalyzer.korovnichenko.repository.CipherRegistry;
import com.javarush.cryptoanalyzer.korovnichenko.repository.EncryptionAlgorithm;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.CipherService;
import com.javarush.cryptoanalyzer.korovnichenko.services.ciphers.CipherServiceImpl;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class EncryptFileFunction implements Function {
    public static final int INPUT_FILE_PATH_INDEX = 1;
    public static final int KEY_INDEX = 2;
    public static final int ALGORITHM_INDEX = 3;
    public static final int ALPHABET_INDEX = 4;
    public static final String ENCRYPTED = "[ENCRYPTED]";
    public static final String EMPTY_FILE = "Empty file: ";
    public static final String IO_ERROR_MESSAGE = "Can't process file:";
    public static final String ENCRYPT_ERROR_MESSAGE = "Encryption finished with error: ";
    private static final int LINES_TO_ANALYSE_ALPHABET = 100;

    private CipherService cipherService;
    private String fileInputPath;
    private String fileOutputPath;

    @Override
    public Result execute(String[] parameters) {
        fileInputPath = parameters[INPUT_FILE_PATH_INDEX];
        fileOutputPath = insertTagToPath(fileInputPath,ENCRYPTED);
        String key = parameters[KEY_INDEX];
        Cipher cipher = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileInputPath), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     new FileOutputStream(fileOutputPath), StandardCharsets.UTF_8))) {

            //detect crypto algorithm from DB or by default: CAESAR
            EncryptionAlgorithm algorithm = parameters.length >= 4
                    ? CipherRegistry.findAlgorithmByName(parameters[ALGORITHM_INDEX])
                    : EncryptionAlgorithm.CAESAR;

            //autodetect alphabet by analysing N first lines in file or error occurred
            String alphabetParam = parameters.length >= 5 ? parameters[ALPHABET_INDEX] : DEFAULT_ALPHABET;
            String detectedAlphabetFromDB = DEFAULT_ALPHABET.equalsIgnoreCase(alphabetParam)
                    ? Alphabets.autodetectAlphabetType(fileInputPath, LINES_TO_ANALYSE_ALPHABET)
                    : Alphabets.findAlphabetByName(alphabetParam);

            cipher = new Cipher(fileInputPath, fileOutputPath, key, algorithm, detectedAlphabetFromDB);

            cipherService = new CipherServiceImpl(cipher);

            String firstLine = reader.readLine();
            if (firstLine == null) {
                return new Result(ERROR, cipher, new ApplicationException(EMPTY_FILE + fileInputPath));
            }

            cipher.setText(firstLine);
            // firstLine has already read - encrypting it...
            writer.write(cipherService.encrypt(cipher.getKey()));

            String line;
            while ((line = reader.readLine()) != null) {
                cipher.setText(line);
                writer.newLine();
                writer.write(cipherService.encrypt(cipher.getKey()));
            }
            writer.flush();

            return new Result(SUCCESS, cipher);
        } catch (IOException e) {
            return new Result(ERROR, cipher, new ApplicationException(IO_ERROR_MESSAGE + e.getMessage()));
        } catch (Exception e) {
            return new Result(ERROR, cipher, new ApplicationException(ENCRYPT_ERROR_MESSAGE + e.getMessage()));
        }
    }

    //Insert to the end of file name [ENCRYPTED] tag
    private String insertTagToPath(String filename, String tag) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return filename + tag;
        }
        return filename.substring(0, dotIndex) + tag + filename.substring(dotIndex);
    }
}
