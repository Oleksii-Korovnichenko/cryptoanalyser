package com.javarush.cryptoanalyzer.korovnichenko.ui;

import com.javarush.cryptoanalyzer.korovnichenko.model.Result;

public interface UI {
    String [] getUIParameters();
    void printResult(Result result);
}
