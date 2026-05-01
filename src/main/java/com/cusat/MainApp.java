package com.cusat;

import com.cusat.core.ScanOrchestrator;
import com.cusat.input.InputValidator;
import com.cusat.input.ScanRequest;
import com.cusat.model.ScanResult;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ScanOrchestrator orchestrator = new ScanOrchestrator();

        System.out.println("=== CuSAT Security Scanner ===");
        System.out.println("Type 'help' for commands or 'exit' to quit.\n");

        while (true) {
            System.out.print("CuSAT> ");
            String input = InputValidator.normalize(scanner.nextLine());

            if (InputValidator.isExitCommand(input)) {
                System.out.println("Exiting CuSAT...");
                break;
            }

            if (InputValidator.isHelpCommand(input)) {
                printHelp();
                continue;
            }

            if (input.isEmpty()) {
                System.out.println("Please enter a valid IPv4 address.");
                continue;
            }

            if (!InputValidator.isSupportedInput(input)) {
                System.out.println("Unsupported input. Only 'help', 'exit', and IPv4 addresses are allowed.");
                continue;
            }

            ScanRequest request = new ScanRequest(input);
            request.setMaxThreads(10);

            ScanResult result = orchestrator.executeScan(request);

            if (result != null) {
                System.out.println("\nScan completed.\n");
            } else {
                System.out.println("Scan failed.\n");
            }
        }

        scanner.close();
    }

    private static void printHelp() {
        System.out.println("\n=== CuSAT Help ===");
        System.out.println("Usage:");
        System.out.println("  Enter an IPv4 address to scan.");
        System.out.println("Commands:");
        System.out.println("  help  - Show this help menu");
        System.out.println("  exit  - Exit the program");
        System.out.println("\nExample:");
        System.out.println("  CuSAT> 127.0.0.1");
        System.out.println("\nNote:");
        System.out.println("  Hostnames, domain names, and URLs are not supported in this build.");
        System.out.println("=================================\n");
    }
}
