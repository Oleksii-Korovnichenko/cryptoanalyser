package com.javarush.cryptoanalyzer.korovnichenko.services.functions;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;

import static com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode.ERROR;

public class UnsupportedFunction implements Function {
    @Override
    public Result execute(String[] parameters) {
        return new Result(ERROR,
                new ApplicationException("Trying to use unsupported function!"));
    }
}
