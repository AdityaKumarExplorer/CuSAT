# CuSAT Viva and Cross Questions

This document is for project defense preparation. It is designed to help a student explain the project clearly and show genuine understanding of the codebase, architecture, and decisions.

## 1. One-Line Project Summary

**Question:** What is CuSAT in one sentence?

**Answer:**  
CuSAT is a Java-based defensive security assessment tool that scans common TCP ports, applies a rule engine to the results, and generates a human-readable risk report.

## 2. Architecture Questions

**Question:** Why did you separate `MainApp` and `ScanOrchestrator`?

**Answer:**  
`MainApp` handles user interaction only, while `ScanOrchestrator` handles the actual scanning workflow. This separation reduces coupling and makes the scan pipeline reusable from both the CLI and the web interface.

**Question:** Why did you create `ScanRequest` instead of passing raw values everywhere?

**Answer:**  
`ScanRequest` groups the target, timeout, port list, and concurrency settings into one object. That keeps method signatures cleaner and makes the code easier to extend later.

**Question:** Why are there separate packages like `input`, `scanner`, `logic`, `model`, and `report`?

**Answer:**  
Each package has one responsibility. `input` handles validation and request structure, `scanner` handles network checks, `logic` handles assessment, `model` stores data, and `report` handles output. This makes the code easier to navigate and explain.

## 3. Scanning Logic Questions

**Question:** What does `HostDiscovery` do?

**Answer:**  
It estimates whether the host is reachable before and during scanning. It first uses `isReachable()`, then tries several TCP fallback ports so that a host is less likely to be incorrectly marked unreachable.

**Question:** Why is `PortScanner` using `ExecutorService`?

**Answer:**  
The original scan was sequential. `ExecutorService` allows bounded concurrency, which improves scan speed without creating one thread per port and wasting system resources.

**Question:** Why bounded concurrency instead of unlimited threads?

**Answer:**  
Unlimited threads are inefficient and can cause unnecessary overhead. A bounded thread pool gives predictable performance and better resource control.

**Question:** What type of scan is this?

**Answer:**  
It is a TCP connect scan. The scanner attempts real socket connections to target ports and classifies them as open, closed, or filtered.

**Question:** Can this scan be detected by other machines?

**Answer:**  
Yes. TCP connect scanning creates real connection attempts, so it can appear in firewall logs, server logs, IDS tools, and other network monitoring systems.

## 4. Input and Validation Questions

**Question:** Why did you restrict the input to IPv4 only?

**Answer:**  
The current build was stabilized around IPv4 to keep validation strict and predictable. Hostnames and URLs were blocked intentionally because the current project goal is a reliable, constrained assessment flow.

**Question:** Then why does `Target` still contain hostname-related logic?

**Answer:**  
That part reflects an earlier and possible future direction of the project. The validator now blocks unsupported input at the interface level, but the underlying target model still shows how the project could expand later.

## 5. Rule Engine and Risk Questions

**Question:** Why use a rule engine?

**Answer:**  
Raw port states are technical facts, but they are not automatically useful to a non-expert. The rule engine translates those facts into findings such as exposed SMB, RDP, or database services.

**Question:** What issue did you fix in `RiskScorer`?

**Answer:**  
Originally, one `HIGH` finding could still produce an overall `LOW` risk because the code required at least two high findings. I corrected that so one high-severity finding now raises the overall risk to `HIGH`.

**Question:** Why separate `RuleEngine` and `RiskScorer`?

**Answer:**  
`RuleEngine` creates findings, while `RiskScorer` summarizes the overall severity. Keeping them separate makes the logic easier to maintain and easier to improve independently.

## 6. Reporting Questions

**Question:** How are reports generated?

**Answer:**  
`ReportGenerator` coordinates reporting. `TextReportWriter` builds one report string and uses it both for console output and for file export.

**Question:** Why use the custom report name format?

**Answer:**  
The project requirement was to make reports easy to identify and sort by target and scan time. That is why the format is `Report(CuSAT)[IP][date][time].txt`.

**Question:** Why use `StringBuilder` in report generation?

**Answer:**  
Because it is more efficient than repeatedly concatenating many strings when building a large report body.

## 7. Web GUI Questions

**Question:** How does the webpage connect to the Java project?

**Answer:**  
The webpage is only a frontend. `WebGuiServer` serves the page and exposes `/api/scan`, which creates a `ScanRequest`, runs `ScanOrchestrator`, and returns JSON back to the browser.

**Question:** Why didn’t you use Spring Boot?

**Answer:**  
For this stage, I wanted a lightweight web layer that reused the scanner with minimal framework overhead. The built-in Java HTTP server was enough for a basic GUI.

**Question:** What command should be used to run the web GUI?

**Answer:**  
Use:

```powershell
mvn exec:java@run-web-gui
```

That avoids PowerShell parsing issues that can happen with the property override form.

## 8. Data Model Questions

**Question:** Why use `record` for `Finding` and `ServiceInfo`?

**Answer:**  
They are small immutable data carriers. `record` is concise and fits the structure well.

**Question:** Why use `enum` for `RiskLevel`?

**Answer:**  
Because severity should come from a controlled set of values, not arbitrary strings. `enum` improves readability and reduces mistakes.

## 9. Testing Questions

**Question:** What tests exist now?

**Answer:**  
The current test suite covers IPv4-only input validation, risk scoring behavior, report filename format, local open-port detection, and network utility behavior.

**Question:** Why were tests added only now?

**Answer:**  
The original project was largely prototype-driven. Once the scan flow stabilized, I converted the empty test stubs into meaningful JUnit tests so the core behavior could be verified.

## 10. Ownership and Understanding Questions

**Question:** If you used AI while building this, how do you prove you understand the project?

**Answer:**  
By being able to explain the architecture, the scan flow, the reason for each package, the key bug fixes, the tradeoffs in port scanning, the report format, the web API flow, and the phase 2 roadmap without reading from the screen.

**Question:** What decisions in this project are actually yours?

**Answer:**  
The key project decisions are mine: making it non-exploitative, using a rule-based assessment layer, keeping a modular package structure, limiting the current build to IPv4 input, defining the report format, and setting the phase 2 direction.

**Question:** What is the strongest answer if someone says this was only vibe-coded?

**Answer:**  
I can say the implementation was AI-assisted, but the project structure, constraints, fixes, validation rules, reporting format, and roadmap were actively reviewed and directed by me. I can also explain each module and why it exists.

## 11. Strong “Improve Next” Questions

**Question:** What should phase 2 focus on?

**Answer:**  
1. Move rules into configuration or JSON.  
2. Add structured JSON reporting as the main export format.  
3. Integrate deeper service fingerprinting through banners, HTTP inspection, and TLS inspection.  
4. Generate PDF and DOCX reports from structured results.  
5. Add stronger authorization and audit controls for wider scan scopes.

**Question:** What is the main limitation today?

**Answer:**  
The project is still a prototype. It does TCP connect scanning and rule-based reporting well enough for demonstration, but it does not yet do advanced fingerprinting, structured reporting, or richer authorization controls.

## 12. Quick Revision Set

Before presenting, make sure you can answer these without hesitation:

- What is scanned?
- Why only IPv4 right now?
- What does `ScanOrchestrator` do?
- Why use `ExecutorService`?
- What bug was fixed in `RiskScorer`?
- How are reports named and saved?
- How does the web GUI call the scanner?
- What information can another machine detect from this scanner?
- What is phase 2?
