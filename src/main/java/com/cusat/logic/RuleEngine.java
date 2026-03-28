package com.cusat.logic;

import com.cusat.model.ScanResult;

/**
 * Applies rules to a scan result and triggers scoring.
 * Phase 1: simple delegation to RiskScorer.
 * Phase 2+: load rules from config, support custom/priority rules, chaining.
 */
public class RuleEngine {

    private final RiskScorer riskScorer;

    public RuleEngine() {
        this.riskScorer = new RiskScorer();
    }

    public void evaluate(ScanResult result) {
        if (result == null || !result.isHostReachable()) {
            return;
        }

        // Phase 1: direct call to scorer
        riskScorer.score(result);

        // Phase 2 placeholder: add more complex rule chaining, logging, etc.
        // Example future: for (Rule rule : loadedRules) { if (rule.appliesTo(result)) { ... } }
    }
}