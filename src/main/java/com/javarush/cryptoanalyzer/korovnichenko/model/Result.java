package com.javarush.cryptoanalyzer.korovnichenko.model;

import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode;

public class Result {
    private ResultCode resultCode;
    private ApplicationException applicationException;
    private Cipher usedCipher;

    public Result(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Result(ResultCode resultCode, Cipher usedCipher) {
        this.resultCode = resultCode;
        this.usedCipher = usedCipher;
    }

    public Result(ResultCode resultCode, ApplicationException applicationException) {
        this.resultCode = resultCode;
        this.applicationException = applicationException;
    }

    public Result(ResultCode resultCode, Cipher usedCipher, ApplicationException applicationException) {
        this.resultCode = resultCode;
        this.usedCipher = usedCipher;
        this.applicationException = applicationException;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public ApplicationException getApplicationException() {
        return applicationException;
    }

    public Cipher getUsedCipher() {
        return usedCipher;
    }
}
