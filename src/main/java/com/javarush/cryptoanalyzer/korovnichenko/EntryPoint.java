package com.javarush.cryptoanalyzer.korovnichenko;

import com.javarush.cryptoanalyzer.korovnichenko.app.Application;
import com.javarush.cryptoanalyzer.korovnichenko.controller.MainController;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.ui.ConsoleUI;
import com.javarush.cryptoanalyzer.korovnichenko.ui.GUI;
import com.javarush.cryptoanalyzer.korovnichenko.ui.UI;

import java.util.Scanner;

import static com.javarush.cryptoanalyzer.korovnichenko.constants.ConsoleUIConstants.*;

public class EntryPoint {
    public static void main(String[] args) {

        if (args != null && args.length >= 3) {
            runNoConsoleMode(args);
        } else {
            tryRunWithFXGuiMode(args);
        }
    }

    // run app with args from command line without ui
    public static void runNoConsoleMode(String[] args) {
        UI consoleUI = new ConsoleUI(args);
        MainController controller = new MainController(consoleUI);
        Application application = new Application(controller);
        Result result = application.run();
        application.printResult(result);
    }

    // run App with ConsoleUI without args
    public static void runConsoleMode() {
        UI consoleUI = new ConsoleUI();
        MainController controller = new MainController(consoleUI);
        Application application = new Application(controller);
        Result result = application.run();
        application.printResult(result);
    }

    public static void tryRunWithFXGuiMode(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println(CHOOSE_THE_MODE);
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase(EXIT)) {
                System.out.println(EXIT_MESSAGE);
                break;
            }

            if (input.toUpperCase().equals(GUI_MODE)) {
                System.out.println(LAUNCH_GUI_MESSAGE);
                try {
                    GUI.launchUI(args);
                } catch (NoClassDefFoundError error) {
                    System.out.println(GUI_LAUNCH_PROBLEM);
                }
            } else {
                runConsoleMode();
            }
        }
        scanner.close();
    }

}
