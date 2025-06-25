package org.chronos;

import org.chronos.service.CLIService;
import org.chronos.service.DispatchCenter;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DispatchCenter dispatchCenter = new DispatchCenter();
        CLIService commandHandler = new CLIService(dispatchCenter);

        System.out.println("Chronos Couriers CLI Started. Enter commands:");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit")) break;
            try {
                commandHandler.handle(line);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}