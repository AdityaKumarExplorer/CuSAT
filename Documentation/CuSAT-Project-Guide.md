# CuSAT Project Guide

## 1. Purpose

CuSAT is a Java-based custom security assessment tool intended for defensive, authorized exposure analysis. The project focuses on identifying reachable hosts, checking common TCP ports, translating exposed services into findings through a rule engine, and generating human-readable reports.

This guide acts as the technical documentation for the current `CuSAT - Copy` project.

## 2. High-Level Architecture

```text
User Input
   |
MainApp / WebGuiServer
   |
ScanRequest -> Target
   |
ScanOrchestrator
   |-- HostDiscovery
   |-- PortScanner
   |-- RuleEngine -> RiskScorer
   `-- ReportGenerator -> TextReportWriter
```

## 3. Directory Structure

```text
src/main/java/com/cusat
├── MainApp.java
├── core
│   └── ScanOrchestrator.java
├── input
│   ├── InputValidator.java
│   ├── ScanRequest.java
│   └── Target.java
├── logic
│   ├── AssessmentResult.java
│   ├── RiskScorer.java
│   ├── Rule.java
│   └── RuleEngine.java
├── model
│   ├── Finding.java
│   ├── PortInfo.java
│   ├── RiskLevel.java
│   ├── ScanResult.java
│   └── ServiceInfo.java
├── report
│   ├── ReportGenerator.java
│   ├── TextReportWriter.java
│   └── TimelineBuilder.java
├── scanner
│   ├── BannerGrabber.java
│   ├── HostDiscovery.java
│   ├── HttpInspector.java
│   ├── IScanner.java
│   ├── PortScanner.java
│   ├── Scanner.java
│   ├── ServiceDetector.java
│   └── TlsInspector.java
├── util
│   ├── Constants.java
│   ├── NetworkUtils.java
│   └── TimeUtils.java
└── web
    └── WebGuiServer.java

src/main/resources/web
├── index.html
├── styles.css
└── app.js
```

## 4. Runtime Flow

### CLI Flow

1. `MainApp` reads terminal input.
2. `InputValidator` checks whether the input is `help`, `exit`, or a valid IPv4 address.
3. `ScanRequest` captures the target, timeout, port list, and concurrency settings.
4. `ScanOrchestrator` coordinates the scan pipeline.
5. `HostDiscovery` checks whether the host appears reachable.
6. `PortScanner` scans the configured port list.
7. `RuleEngine` converts scan facts into findings.
8. `RiskScorer` computes the overall risk.
9. `ReportGenerator` prints the report and exports it to disk.

### Web Flow

1. `WebGuiServer` serves the page and the scan API.
2. `index.html` renders the GUI.
3. `app.js` submits an IPv4 value to `/api/scan`.
4. The server validates the input, runs the same scan path, and returns JSON.
5. The browser renders results on the page.

## 5. Information Collected and Detectability

### Information CuSAT Collects

- target IPv4 address
- scan timestamp
- host reachability state
- port state per scanned port
- known service name from port mapping
- generated findings
- overall risk level
- scan duration
- timeline events
- exported report file path

### Whether Other Machines Can Detect It

Yes. The project uses TCP connect scanning, which can often be detected by:

- target host service logs
- local or remote firewall logs
- IDS/IPS systems
- network monitoring tools
- server-side connection counters or audit logs

The current build does not try to hide itself. That is intentional.

## 6. Why Key Java Syntax Choices Were Used

- `final` fields: used to lock dependencies or constant-style object state after construction.
- `private static final`: used for shared constants such as report paths, date formatters, and defaults.
- `record`: used in `Finding` and `ServiceInfo` to model small immutable data carriers concisely.
- `enum`: used in `RiskLevel` for controlled severity values.
- `Collections.unmodifiableList(...)`: used to avoid accidental mutation of shared lists.
- `ExecutorService` and `Future`: used in `PortScanner` to improve wall-clock scan time while keeping the logic manageable.
- `try-with-resources`: used with `Socket`, `InputStream`, and file operations to guarantee cleanup.
- `Path` and `Files`: used instead of string-only file handling because they are safer and clearer for filesystem operations.
- `StringBuilder`: used when constructing reports or JSON to avoid repeated intermediate string allocations.

## 7. Module-by-Module Explanation

### `MainApp`

Role:
- terminal entry point for the scanner

Why it is written this way:
- keeps CLI logic lightweight
- hands scan execution to `ScanOrchestrator`
- avoids mixing scanning logic directly into the UI loop

### `core/ScanOrchestrator`

Role:
- central coordinator for the scan workflow

Why it matters:
- separates workflow control from scanning details
- makes it easier to swap or extend scanners later
- central place for validation, timing, and report triggering

### `input/InputValidator`

Role:
- blocks unsupported input types

Why it matters:
- the current project does not fully support hostnames or URLs
- restricting the accepted input avoids confusing failure paths

### `input/ScanRequest`

Role:
- bundles user-provided target and scanner settings

Why it matters:
- avoids passing many loose parameters around
- creates a single request object for orchestrator and scanners

### `input/Target`

Role:
- represents the scan target and stores resolution state

Current note:
- this class still supports hostname resolution internally, but the CLI and web validator intentionally block hostnames in the current build

### `logic/RuleEngine`

Role:
- maps observed conditions into findings

Why it matters:
- scanning alone gives raw data
- the rule engine converts raw port exposure into security meaning

### `logic/RiskScorer`

Role:
- derives a single overall risk value from the findings list

Important recent fix:
- one `HIGH` finding now results in overall `HIGH` instead of incorrectly staying `LOW`

### `model/*`

Role:
- store scan results, findings, services, and severity levels

Why this design helps:
- keeps scanner code and reporting code working with the same stable data model

### `report/ReportGenerator`

Role:
- centralizes report output calls

Why it matters:
- keeps report coordination in one place
- allows future JSON/PDF/DOCX outputs to be added cleanly

### `report/TextReportWriter`

Role:
- converts `ScanResult` into a formatted report string and writes it to disk

Why it matters:
- one builder method drives both console output and file export
- current report filename format is controlled here

### `report/TimelineBuilder`

Role:
- records ordered scan events

Why it matters:
- useful for demos, debugging, and understanding execution flow

### `scanner/HostDiscovery`

Role:
- estimates host reachability before and during scanning

Why it matters:
- avoids relying only on ICMP-style checks
- uses TCP fallback ports to reduce false negatives

### `scanner/PortScanner`

Role:
- scans the configured TCP ports

Why it matters:
- uses bounded concurrency through a fixed thread pool
- associates service names with open ports

### `scanner/ServiceDetector`

Role:
- maps open ports and optional banners to service labels

Current status:
- partially used now through `PortScanner`
- deeper banner-based logic is still Phase 2 work

### `scanner/BannerGrabber`, `HttpInspector`, `TlsInspector`

Role:
- future-oriented enrichment modules

Current status:
- present in the repo
- not fully integrated into the main scanning path yet

### `util/Constants`

Role:
- single source of truth for shared ports, timeouts, thread defaults, and report text

### `util/NetworkUtils`

Role:
- input-oriented network helpers such as IPv4 validation and hostname handling

### `util/TimeUtils`

Role:
- timestamp and duration formatting

### `web/WebGuiServer`

Role:
- serves the static web page and exposes an HTTP API for scans

Why it matters:
- turns the existing scanner into a local browser-usable tool without introducing a heavy framework

## 8. Function Description Table

| Class | Function | Purpose | Notes |
|---|---|---|---|
| `MainApp` | `main` | runs the CLI loop | keeps UI separate from scan logic |
| `MainApp` | `printHelp` | prints supported commands | documents IPv4-only limitation |
| `InputValidator` | `isHelpCommand` | checks for `help` | case-insensitive |
| `InputValidator` | `isExitCommand` | checks for `exit` | case-insensitive |
| `InputValidator` | `isValidIpv4Target` | checks IPv4 validity | delegates to `NetworkUtils` |
| `InputValidator` | `isSupportedInput` | enforces current accepted input | command or IPv4 only |
| `InputValidator` | `normalize` | trims and null-guards input | keeps checks consistent |
| `ScanRequest` | constructors | create scan request objects | wraps target and options |
| `ScanRequest` | `getPortsToScan` | returns custom or default ports | protects defaults |
| `ScanRequest` | `setConnectTimeoutMs` | clamps timeout values | avoids dangerous extremes |
| `ScanRequest` | `setMaxThreads` | clamps thread count | keeps concurrency bounded |
| `Target` | `validateAndResolve` | resolves and validates target | internal helper |
| `ScanOrchestrator` | `executeScan` | runs the main scan workflow | core coordination method |
| `ScanOrchestrator` | `executeAsyncScan` | async wrapper | currently basic thread launch |
| `RuleEngine` | `evaluate` | creates findings from scan data | main assessment function |
| `RiskScorer` | `score` | computes overall risk | fixed in current build |
| `ReportGenerator` | `generateConsoleReport` | prints and exports report | triggers text export |
| `TextReportWriter` | `write` | prints built report | console path |
| `TextReportWriter` | `writeToFile` | saves report to disk | uses requested filename format |
| `TextReportWriter` | `buildReportFileName` | builds `Report(CuSAT)[IP][date][time]` | current naming rule |
| `TextReportWriter` | `buildReport` | creates report string | reused by print and export |
| `TimelineBuilder` | `addEvent` | logs timeline entries | thread-safe list backend |
| `TimelineBuilder` | `clear` | resets prior events | used before each scan |
| `TimelineBuilder` | `printTimeline` | prints ordered events | presentation and debugging |
| `HostDiscovery` | `scan` | checks reachability | ICMP-style + TCP fallback |
| `HostDiscovery` | `probeTcpPorts` | checks fallback ports | reduces false negatives |
| `PortScanner` | `scan` | runs port scan and returns `ScanResult` | orchestrates scan workers |
| `PortScanner` | `scanPorts` | executes tasks with executor | main concurrent block |
| `PortScanner` | `createPortTask` | creates per-port task | keeps task creation isolated |
| `PortScanner` | `checkPort` | performs one TCP connect test | per-port low-level logic |
| `ServiceDetector` | `detect` | infers service label/version | currently basic mapping |
| `WebGuiServer` | `main` | starts local HTTP server | entry point for GUI mode |
| `WebGuiServer` | `handleScanRequest` | serves `/api/scan` | validates input and returns JSON |
| `WebGuiServer` | `serveStatic` | serves HTML/CSS/JS files | local frontend delivery |
| `WebGuiServer` | `loadAsset` | loads web assets | classpath + filesystem fallback |
| `WebGuiServer` | `parseQuery` | parses URL query params | light manual parser |
| `WebGuiServer` | `toJson` | serializes result to JSON | manual builder to avoid extra dependency |
| `WebGuiServer` | `escapeJson` | escapes string content | protects JSON output |
| `WebGuiServer` | `writeResponse` | writes HTTP response | shared response helper |

## 9. Variable Description Table

| Class | Variable | Type | Purpose |
|---|---|---|---|
| `ScanRequest` | `target` | `Target` | requested scan target |
| `ScanRequest` | `portsToScan` | `List<Integer>` | active custom/default port list |
| `ScanRequest` | `connectTimeoutMs` | `int` | connect timeout in milliseconds |
| `ScanRequest` | `aggressive` | `boolean` | reserved scan-mode flag |
| `ScanRequest` | `maxThreads` | `int` | concurrency cap |
| `Target` | `rawInput` | `String` | original user input |
| `Target` | `resolvedIp` | `String` | resolved IPv4 target |
| `Target` | `isValid` | `boolean` | validation state |
| `Target` | `isPrivateOrLocal` | `boolean` | local/private-range hint |
| `ScanOrchestrator` | `ruleEngine` | `RuleEngine` | assessment dependency |
| `ScanOrchestrator` | `reportGenerator` | `ReportGenerator` | output dependency |
| `RiskScorer` | local counters | `int` | count severity buckets |
| `ScanResult` | `target` | `String` | final scan target |
| `ScanResult` | `hostReachable` | `boolean` | reachability state |
| `ScanResult` | `ports` | `List<PortInfo>` | scanned port results |
| `ScanResult` | `durationMs` | `long` | total scan duration |
| `ScanResult` | `scanTimestamp` | `String` | human-readable time |
| `ScanResult` | `overallRisk` | `RiskLevel` | final severity |
| `ScanResult` | `findings` | `List<Finding>` | assessment output |
| `PortInfo` | `port` | `int` | TCP port number |
| `PortInfo` | `open` | `boolean` | open/closed state flag |
| `PortInfo` | `status` | `String` | status label |
| `PortInfo` | `serviceName` | `String` | inferred service |
| `PortInfo` | `banner` | `String` | future banner text |
| `ReportGenerator` | `REPORT_DIRECTORY` | `Path` | report export folder |
| `ReportGenerator` | `textWriter` | `TextReportWriter` | text export dependency |
| `TextReportWriter` | `DATE_FORMAT` | `DateTimeFormatter` | filename date formatter |
| `TextReportWriter` | `TIME_FORMAT` | `DateTimeFormatter` | filename time formatter |
| `TimelineBuilder` | `events` | `List<String>` | ordered event log |
| `TimelineBuilder` | `SEPARATOR` | `String` | display separator |
| `HostDiscovery` | `timeoutMs` | `int` | reachability timeout |
| `HostDiscovery` | `probePorts` | `List<Integer>` | TCP fallback ports |
| `PortScanner` | `portsToScan` | `List<Integer>` | port workload |
| `PortScanner` | `timeoutMs` | `int` | per-port timeout |
| `PortScanner` | `maxThreads` | `int` | executor size |
| `PortScanner` | `serviceDetector` | `ServiceDetector` | service label helper |
| `Constants` | `COMMON_PORTS` | `List<Integer>` | default scan profile |
| `Constants` | `DEFAULT_CONNECT_TIMEOUT_MS` | `int` | shared timeout default |
| `Constants` | `DEFAULT_THREAD_POOL_SIZE` | `int` | shared concurrency default |
| `WebGuiServer` | `DEFAULT_PORT` | `int` | web GUI server port |

## 10. How to Run the Project

### CLI

```powershell
cd "C:\Personal Corner\College\Java Notes & Projects\CuSAT - Copy"
mvn test
mvn exec:java
```

### Web GUI

```powershell
cd "C:\Personal Corner\College\Java Notes & Projects\CuSAT - Copy"
mvn test
mvn exec:java@run-web-gui
```

Then open:

```text
http://localhost:8085
```

Note:
- the dedicated Maven execution avoids PowerShell parsing issues that can happen with `-Dexec.mainClass=...`

## 11. Java Project Recognition

This repository includes:

- `pom.xml`
- `.project`
- `.classpath`
- `.settings/org.eclipse.jdt.core.prefs`
- `.vscode/settings.json`
- `.vscode/extensions.json`

These files help IDEs recognize the workspace as a Java/Maven project and reduce the chance of “non-project file” errors.

## 12. Security and Ethical Constraints

Keep the project within these boundaries:

- only scan systems you own or are explicitly authorized to assess
- do not add exploitation or payload modules
- do not add credential attacks
- do not add stealth or evasion capabilities
- log and present scan behavior honestly
- prefer defensive reporting and hardening guidance

## 13. Phase 2 Plan

### 13.1 Rule System

- move rules out of hardcoded Java into JSON or config
- support custom user-defined rules
- allow finding categories and priorities

### 13.2 Scanner Enrichment

- integrate `BannerGrabber`
- integrate `HttpInspector`
- integrate `TlsInspector`
- improve service/version inference

### 13.3 Reporting

- create a JSON report as the canonical export format
- generate PDF and DOCX reports from JSON
- support report filtering by severity
- include findings grouped by severity and category

### 13.4 Scope and Safety

- explicit authorization settings for non-local scanning
- safer scan profiles
- stronger rate limiting and audit logging
- optional allowlist of private network ranges

### 13.5 Web Interface

- multi-panel dashboard
- saved scan history
- downloadable reports
- risk visualizations
- authentication if the UI ever becomes remotely exposed

### 13.6 Testing

- add more unit tests around orchestrator behavior
- add integration tests for the web API
- add filesystem tests for report export
- add regression tests for rule behavior

## 14. Prompt Context

See:

[PROJECT_CONTEXT_PROMPT.md](./PROJECT_CONTEXT_PROMPT.md)

That file captures the current project status and the progress context so future work can start from the same baseline.
