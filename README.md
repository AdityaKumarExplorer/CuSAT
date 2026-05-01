<<<<<<< Updated upstream
# 🛡️ CuSAT – Custom Security Assessment Tool
=======
# CuSAT
>>>>>>> Stashed changes

Custom Security Assessment Tool

CuSAT is a Java-based defensive security assessment project focused on identifying exposed TCP services, applying a rule-based risk engine, and producing readable scan reports. It is intentionally non-exploitative: the project performs exposure discovery and reporting, not payload delivery or exploitation.

## Current Scope

<<<<<<< Updated upstream
CuSAT is a **Java-based defensive cybersecurity tool** designed to analyze **network exposure**.

This is a **Self Understanding Edition**, meaning:

* It avoids intrusive or exploitative techniques
* It focuses on **learning and defensive awareness**
=======
This repository is still an early prototype, but the local `CuSAT - Copy` build now supports:
- IPv4-only CLI input
- concurrent TCP connect scanning
- rule-based finding generation
- console reporting
- text report export
- a lightweight browser GUI served by a local Java HTTP server
- starter JUnit coverage for key flows

## Project Structure

```text
src/main/java/com/cusat
├── MainApp.java
├── core
├── input
├── logic
├── model
├── report
├── scanner
├── util
└── web
>>>>>>> Stashed changes

src/main/resources/web
├── index.html
├── styles.css
└── app.js
```

## What the Tool Does

1. Accepts an IPv4 address from the CLI or web GUI.
2. Validates the input and rejects unsupported values.
3. Performs host discovery using `isReachable()` with TCP fallback probes.
4. Scans a bounded list of common TCP ports using a thread pool.
5. Applies hardcoded risk rules to the open-port data.
6. Calculates an overall risk value.
7. Prints the report and writes a text copy to disk.

## Input Rules

The current build accepts:
- `help`
- `exit`
- IPv4 addresses such as `127.0.0.1` or `192.168.1.10`

The current build does not accept:
- hostnames
- domain names
- URLs

## Default Port Coverage

The default scan profile includes:

`21, 22, 23, 25, 53, 80, 110, 135, 139, 143, 443, 445, 993, 995, 1433, 3306, 3389, 5900, 8080`

## Report Output

Text reports are written to:

```text
output/reports/
```

Filename format:

```text
Report(CuSAT)[IP][date][time].txt
```

Example:

```text
Report(CuSAT)[127.0.0.1][2026-04-30][16-09-37].txt
```

## Information Collected by the Scanner

The current modules can acquire:
- target IPv4 address
- host reachability state
- scan timestamp
- scan duration
- open, closed, or filtered status per scanned TCP port
- inferred service name from known port mappings
- rule-engine findings
- overall risk classification
- timeline events during execution

## Can Other Machines Detect the Scan?

Yes, potentially.

What can be noticed by another machine:
- repeated TCP connection attempts to the scanned ports
- timing patterns from concurrent scanning
- connection attempts in local firewall, IDS, or server logs
- HTTP/TLS probe behavior in future expanded modules

What is not happening in this project:
- raw-packet stealth scanning
- exploit delivery
- credential attacks
- evasion logic

So the current scanner is detectable in the normal way many TCP connect scans are detectable.

## How to Run

### CLI with Maven

```powershell
cd "C:\Personal Corner\College\Java Notes & Projects\CuSAT - Copy"
mvn test
mvn exec:java
```

### CLI without Maven

```powershell
cd "C:\Personal Corner\College\Java Notes & Projects\CuSAT - Copy"
$files = Get-ChildItem src\main\java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if (Test-Path out) { Remove-Item out -Recurse -Force }
New-Item -ItemType Directory -Path out | Out-Null
javac -d out $files
java -cp out com.cusat.MainApp
```

### Web GUI

Run the web server:

```powershell
cd "C:\Personal Corner\College\Java Notes & Projects\CuSAT - Copy"
mvn test
mvn exec:java@run-web-gui
```

Then open:

```text
http://localhost:8085
```

PowerShell note:
- use `mvn exec:java@run-web-gui`
- this avoids the argument parsing issue that can happen with `-Dexec.mainClass=...`

## How the Webpage Connects to the Project

The GUI is a thin frontend over the Java scanner:

1. `WebGuiServer` starts a local HTTP server on port `8085`.
2. `index.html` serves a browser form for IPv4 input.
3. `app.js` sends the entered IPv4 address to `/api/scan`.
4. The `/api/scan` handler creates a `ScanRequest`.
5. `ScanOrchestrator` runs the existing scanner pipeline.
6. The server returns scan results as JSON.
7. The webpage renders ports, findings, and the timeline.

This means the webpage is not a separate scanner. It is only a user interface over the same Java scanning logic.

If the property-based command form is used in some PowerShell environments, argument parsing can break the `-Dexec.mainClass=...` override. The dedicated Maven execution `mvn exec:java@run-web-gui` avoids that issue.

## Java Project Setup Notes

If your editor says something like:

```text
ScanOrchestrator.java is a non-project file, only syntax errors are reported
```

use the project root folder and not an individual file folder.

This repository now includes:
- `pom.xml`
- `.project`
- `.classpath`
- `.settings/org.eclipse.jdt.core.prefs`
- `.vscode/settings.json`
- `.vscode/extensions.json`

These help Eclipse-compatible tooling and VS Code recognize the folder as a Java/Maven project.

## Ethical Constraints

Use this tool only on systems and networks you own or are explicitly authorized to assess.

Recommended constraints for the project:
- no exploit modules
- no persistence features
- no password spraying or brute force features
- no stealth/evasion additions
- no default wide-area scanning behavior
- explicit user-visible warnings and authorization expectations

## License Recommendation

This project currently has no license file, which means reuse rights are unclear.

Recommended choices:
- `MIT` for the simplest student-project licensing
- `Apache-2.0` if you want explicit patent language
- `GPL-3.0` if you want derivatives to remain open source

For this project, `MIT` is the simplest recommendation.

To implement it:
1. Add a `LICENSE` file at the repository root.
2. Add a short license section to the README.
3. Keep author attribution in the repo metadata or documentation.

## Testing

The current local test suite covers:
- IPv4-only input validation
- risk scoring behavior
- report filename format
- local open-port detection
- network utility behavior

Run:

```powershell
mvn test
```

## Current Limitations

- TCP connect scanning only
- no UDP scanning
- no authenticated checks
- no CVE mapping
- no PDF or DOCX report export from scan results yet
- no persistent database or dashboard backend
- hostnames and URLs are intentionally blocked in this build

## Phase 2 Priorities

1. Externalize rules into JSON or configuration.
2. Integrate banner grabbing and deeper service fingerprinting.
3. Add structured JSON output as the reporting source of truth.
4. Generate PDF and DOCX reports from structured result data.
5. Add explicit authorization safeguards for non-local scanning.
6. Expand the web interface into a fuller dashboard.

## Viva Preparation

If you need to defend the project in a demo or review, be ready to explain:
- why `ScanOrchestrator` exists instead of putting everything in `MainApp`
- why `ScanRequest` is used as a wrapper object
- why TCP connect scanning is detectable
- why `PortScanner` uses a thread pool in the updated build
- why `RiskScorer` had to be fixed
- why hostnames and URLs are blocked in the current build
- how the web GUI is only a frontend over the same Java scan pipeline

Detailed viva-style questions and answers are documented in:
[docs/CuSAT-Project-Guide.md](</C:/Personal Corner/College/Java Notes & Projects/CuSAT - Copy/docs/CuSAT-Project-Guide.md>)

## Author

Aditya Kumar

## Disclaimer

CuSAT is a defensive learning project for authorized exposure assessment and reporting only.
