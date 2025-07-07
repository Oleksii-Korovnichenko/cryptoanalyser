package com.javarush.cryptoanalyzer.korovnichenko.controller;

import com.javarush.cryptoanalyzer.korovnichenko.ui.UI;

public class MainController {
    private UI ui;

    public MainController(UI ui) {
        this.ui = ui;
    }

    public UI getUi() {
        return ui;
    }

}
