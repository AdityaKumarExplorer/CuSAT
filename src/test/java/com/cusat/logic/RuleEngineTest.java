package com.cusat.logic;

import com.cusat.model.PortInfo;
import com.cusat.model.RiskLevel;
import com.cusat.model.ScanResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleEngineTest {

    @Test
    void highSeverityFindingShouldDriveOverallRiskHigh() {
        ScanResult result = new ScanResult("127.0.0.1");
        result.setHostReachable(true);
        result.addPort(openPort(445));

        new RuleEngine().evaluate(result);

        assertEquals(RiskLevel.HIGH, result.getOverallRisk());
        assertEquals(1, result.getFindings().size());
    }

    @Test
    void criticalCombinationShouldEscalateOverallRisk() {
        ScanResult result = new ScanResult("127.0.0.1");
        result.setHostReachable(true);
        result.addPort(openPort(445));
        result.addPort(openPort(3389));

        new RuleEngine().evaluate(result);

        assertEquals(RiskLevel.CRITICAL, result.getOverallRisk());
    }

    private PortInfo openPort(int port) {
        return new PortInfo(port, true, "open");
    }
}
