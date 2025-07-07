package com.javarush.cryptoanalyzer.korovnichenko.services.functions;

import com.javarush.cryptoanalyzer.korovnichenko.model.Result;

public interface Function {
    Result execute(String[] parameters);
}
