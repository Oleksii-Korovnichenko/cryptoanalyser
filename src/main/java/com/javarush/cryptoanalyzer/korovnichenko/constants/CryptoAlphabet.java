package com.javarush.cryptoanalyzer.korovnichenko.constants;

public class CryptoAlphabet {
    public static final String NUMBERS = "0123456789";
    public static final String LATIN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LATIN_WITH_NUMBERS = LATIN + NUMBERS;
    public static final String UKR = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюяАБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";//АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ
    public static final String UKR_WITH_NUMBERS = UKR + NUMBERS;
    public static final String PUNCT = " .,«»':!?\"";
    public static final String MIXED = LATIN + UKR + NUMBERS + PUNCT;
}
