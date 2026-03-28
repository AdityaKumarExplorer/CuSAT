package com.cusat;

import com.cusat.core.ScanOrchestrator;
import com.cusat.input.ScanRequest;
import com.cusat.input.Target;
import com.cusat.model.ScanResult;

public class MainApp {
    public static void main(String[] args) {
        Target target = new Target("127.0.0.1", 1, 1000);

        ScanRequest request = new ScanRequest();
        request.setFastScan(true);

        ScanOrchestrator orchestrator = new ScanOrchestrator();
        ScanResult result = orchestrator.runScan(target, request);

        System.out.println("Scan completed!");
    }
}