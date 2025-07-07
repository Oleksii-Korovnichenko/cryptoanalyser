package com.javarush.cryptoanalyzer.korovnichenko.model;

import com.javarush.cryptoanalyzer.korovnichenko.repository.EncryptionAlgorithm;

public class Cipher {
    private String text;
    private String key;
    private EncryptionAlgorithm algorithm;
    private String alphabetType;
    private String fileOutputPath;
    private String fileInputPath;

    public Cipher() {
    }

    public Cipher(String fileInputPath, String fileOutputPath, String key,
                  EncryptionAlgorithm algorithm, String alphabetType) {
        this.fileInputPath = fileInputPath;
        this.fileOutputPath = fileOutputPath;
        this.key = key;
        this.algorithm = algorithm;
        this.alphabetType = alphabetType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public EncryptionAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(EncryptionAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlphabetType() {
        return alphabetType;
    }

    public void setAlphabetType(String alphabetType) {
        this.alphabetType = alphabetType;
    }

    public String getFileOutputPath() {
        return fileOutputPath;
    }

    public void setFileOutputPath(String fileOutputPath) {
        this.fileOutputPath = fileOutputPath;
    }

    public String getFileInputPath() {
        return fileInputPath;
    }

    public void setFileInputPath(String fileInputPath) {
        this.fileInputPath = fileInputPath;
    }
}
