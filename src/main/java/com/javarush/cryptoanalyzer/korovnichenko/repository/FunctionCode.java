package com.javarush.cryptoanalyzer.korovnichenko.repository;

import com.javarush.cryptoanalyzer.korovnichenko.services.functions.BruteForceFileService;
import com.javarush.cryptoanalyzer.korovnichenko.services.functions.DecryptFileFunction;
import com.javarush.cryptoanalyzer.korovnichenko.services.functions.EncryptFileFunction;
import com.javarush.cryptoanalyzer.korovnichenko.services.functions.Function;
import com.javarush.cryptoanalyzer.korovnichenko.services.functions.UnsupportedFunction;

public enum FunctionCode {
    ENCRYPT(new EncryptFileFunction()),
    DECRYPT(new DecryptFileFunction()),
    BRUTE_FORCE(new BruteForceFileService()),
    UNSUPPORTED_FUNCTION(new UnsupportedFunction());

    private final Function function;

    FunctionCode(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }
}
