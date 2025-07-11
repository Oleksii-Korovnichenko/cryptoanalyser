package com.javarush.cryptoanalyzer.korovnichenko.app;

import com.javarush.cryptoanalyzer.korovnichenko.controller.MainController;
import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.repository.FunctionCode;
import com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode;
import com.javarush.cryptoanalyzer.korovnichenko.services.functions.Function;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.FunctionCodeConstants.BRUTE_FORCE;
import static com.javarush.cryptoanalyzer.korovnichenko.constants.FunctionCodeConstants.DECRYPT;
import static com.javarush.cryptoanalyzer.korovnichenko.constants.FunctionCodeConstants.ENCRYPT;
import static com.javarush.cryptoanalyzer.korovnichenko.constants.FunctionCodeConstants.UNSUPPORTED_FUNCTION;

public class Application {
    public static final int MODE_INDEX = 0;

    private MainController mainController;

    public Application(MainController mainController) {
        this.mainController = mainController;
    }

    public Result run() {
        String[] parameters = mainController.getUi().getUIParameters();
        String mode = parameters[MODE_INDEX];
        Function function = getFunction(mode);
        Result result = function.execute(parameters);
            return result;
    }

    public Function getFunction(String mode) {
        return switch (mode.toUpperCase()) {
            case ENCRYPT -> FunctionCode.valueOf(ENCRYPT).getFunction();
            case DECRYPT -> FunctionCode.valueOf(DECRYPT).getFunction();
            case BRUTE_FORCE -> FunctionCode.valueOf(BRUTE_FORCE).getFunction();
            default -> FunctionCode.valueOf(UNSUPPORTED_FUNCTION).getFunction();
        };
    }

    public void printResult (Result result) {
        mainController.getUi().printResult(result);
    }
}

