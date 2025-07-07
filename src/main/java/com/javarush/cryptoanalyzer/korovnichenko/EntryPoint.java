package com.javarush.cryptoanalyzer.korovnichenko;

import com.javarush.cryptoanalyzer.korovnichenko.app.Application;
import com.javarush.cryptoanalyzer.korovnichenko.controller.MainController;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.ui.ConsoleUI;
import com.javarush.cryptoanalyzer.korovnichenko.ui.GUI;
import com.javarush.cryptoanalyzer.korovnichenko.ui.UI;

import java.util.Scanner;

public class EntryPoint {
    public static void main(String[] args) {
        Application application;
        MainController controller;
        Result result;
        if (args != null && args.length >= 3) {
            // args from command line without ui
            UI consoleUI = new ConsoleUI(args);
            controller = new MainController(consoleUI);
            application = new Application(controller);
            result = application.run();
            application.printResult(result);
        } else {
            Scanner scanner = new Scanner(System.in);
            String input;

            while (true) {
                System.out.println("Choose working mode: type [GUI] or default : [ConsoleUI]");
                System.out.println("Type 'exit' to quit the program.");
                input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                }

                switch (input.toUpperCase()) {
                    case "GUI": {
                        System.out.println("Launching in GUI mode ...");
                        try {
                            GUI.launchUI(args);
                        } catch (NoClassDefFoundError error) {
                            System.out.println("Can't launch GUI mode: \n " +
                                    "Run application with parameters: \n" +
                                    "java --module-path [path for javafx-sdk\\lib] \n" +
                                    "--add-modules javafx.controls,javafx.graphics \n" +
                                    "-jar cryptoanalyzer-ver1.0.jar");
                        }
                        break;
                    }
                    case "":
                    case "CONSOLE_UI": {
                        UI consoleUI = new ConsoleUI();
                        controller = new MainController(consoleUI);

                        application = new Application(controller);
                        result = application.run();
                        application.printResult(result);
                        break;
                    }
                    default: {
                        System.out.println("Invalid input. Type GUI, CONSOLE_UI or exit.");
                        break;
                    }
                }
            }
            scanner.close();
        }
    }
}
