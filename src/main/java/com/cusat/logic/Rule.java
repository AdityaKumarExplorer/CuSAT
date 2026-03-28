package com.cusat.logic;

import com.cusat.model.PortInfo;
import com.cusat.model.RiskLevel;

/**
 * Represents a single security rule for evaluating open ports.
 * Phase 1: hardcoded simple rules.
 * Phase 2+: can be loaded from config, YAML, or scripted.
 */
public class Rule {

    private final String description;
    private final RiskLevel severity;
    private final java.util.function.Predicate<PortInfo> condition;

    public Rule(String description, RiskLevel severity, java.util.function.Predicate<PortInfo> condition) {
        this.description = description;
        this.severity = severity;
        this.condition = condition;
    }

    public boolean appliesTo(PortInfo portInfo) {
        return condition.test(portInfo);
    }

    public String getDescription() {
        return description;
    }

    public RiskLevel getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return severity + ": " + description;
    }
}