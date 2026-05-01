# 🛡️ CuSAT – Custom Security Assessment Tool

Custom Security Assessment Tool

CuSAT is a Java-based defensive security assessment project focused on identifying exposed TCP services, applying a rule-based risk engine, and producing readable scan reports. It is intentionally non-exploitative: the project performs exposure discovery and reporting, not payload delivery or exploitation.

---

## 📋 Table of Contents

- [Current Scope](#current-scope)
- [Project Structure](#project-structure)
- [What the Tool Does](#what-the-tool-does)
- [Input Rules](#input-rules)
- [Default Port Coverage](#default-port-coverage)
- [Report Output](#report-output)
- [Information Collected](#information-collected-by-the-scanner)
- [Detection Notice](#can-other-machines-detect-the-scan)
- [How to Run](#how-to-run)
- [Web GUI](#web-gui)
- [Testing](#testing)
- [Current Limitations](#current-limitations)
- [Roadmap](#roadmap)
- [Ethical Constraints](#ethical-constraints)
- [License](#license)
- [Author](#author)
- [Disclaimer](#disclaimer)

---

## Current Scope

This repository supports:

- ✅ IPv4-only CLI input
- ✅ Concurrent TCP connect scanning
- ✅ Rule-based finding generation
- ✅ Console reporting
- ✅ Text report export
- ✅ Lightweight browser GUI served by a local Java HTTP server
- ✅ JUnit coverage for key flows

## Project Structure

```
CuSAT/
├── src/
│   ├── main/
│   │   ├── java/com/cusat/
│   │   │   ├── MainApp.java
│   │   │   ├── core/           # Scan orchestration
│   │   │   ├── input/          # Input validation
│   │   │   ├── logic/          # Risk scoring & rules
│   │   │   ├── model/          # Data models
│   │   │   ├── report/         # Report generation
│   │   │   ├── scanner/        # Network scanning
│   │   │   ├── util/           # Utilities
│   │   │   └── web/            # Web GUI server
│   │   └── resources/web/
│   │       ├── index.html
│   │       ├── styles.css
│   │       └── app.js
│   └── test/java/com/cusat/    # Unit tests
├── output/reports/             # Generated reports
├── pom.xml                     # Maven configuration
├── README.md
└── LICENSE
```

## What the Tool Does

1. Accepts an IPv4 address from the CLI or web GUI
2. Validates the input and rejects unsupported values
3. Performs host discovery using `isReachable()` with TCP fallback probes
4. Scans a bounded list of common TCP ports using a thread pool
5. Applies hardcoded risk rules to the open-port data
6. Calculates an overall risk value
7. Prints the report and writes a text copy to disk

## Input Rules

**Accepts:**
- `help` - Display help information
- `exit` - Exit the application
- IPv4 addresses (e.g., `127.0.0.1`, `192.168.1.10`)

**Does NOT accept:**
- Hostnames (e.g., `localhost`, `example.com`)
- Domain names
- URLs

## Default Port Coverage

The default scan profile includes:

```
21, 22, 23, 25, 53, 80, 110, 135, 139, 143, 443, 445, 993, 995, 1433, 3306, 3389, 5900, 8080
```

| Port | Service |
|------|---------|
| 21 | FTP |
| 22 | SSH |
| 23 | Telnet |
| 25 | SMTP |
| 53 | DNS |
| 80 | HTTP |
| 110 | POP3 |
| 135 | RPC |
| 139 | NetBIOS |
| 143 | IMAP |
| 443 | HTTPS |
| 445 | SMB |
| 993 | IMAPS |
| 995 | POP3S |
| 1433 | MSSQL |
| 3306 | MySQL |
| 3389 | RDP |
| 5900 | VNC |
| 8080 | HTTP-Alt |

## Report Output

**Location:**
```
output/reports/
```

**Filename format:**
```
Report(CuSAT)[IP][date][time].txt
```

**Example:**
```
Report(CuSAT)[127.0.0.1][2026-04-30][16-09-37].txt
```

## Information Collected by the Scanner

The current modules can acquire:

- Target IPv4 address
- Host reachability state
- Scan timestamp
- Scan duration
- Open, closed, or filtered status per scanned TCP port
- Inferred service name from known port mappings
- Rule-engine findings
- Overall risk classification (Low/Medium/High/Critical)
- Timeline events during execution

## Can Other Machines Detect the Scan?

**Yes, potentially.**

### What can be noticed by another machine:
- Repeated TCP connection attempts to scanned ports
- Timing patterns from concurrent scanning
- Connection attempts in local firewall, IDS, or server logs
- HTTP/TLS probe behavior (future modules)

### What is NOT happening:
- Raw-packet stealth scanning
- Exploit delivery
- Credential attacks
- Evasion logic

The scanner is detectable in the normal way many TCP connect scans are detectable.

## How to Run

### Prerequisites

- Java 11 or higher
- Maven 3.6+ (recommended)

### Option 1: CLI with Maven (Recommended)

```bash
# Clone the repository
git clone https://github.com/AdityaKumarExplorer/CuSAT.git
cd CuSAT

# Compile and run tests
mvn clean compile
mvn test

# Run the application
mvn exec:java
```

### Option 2: CLI without Maven

```bash
# Compile all Java files
javac -d out $(find src/main/java -name "*.java")

# Run the application
java -cp out com.cusat.MainApp
```

### Option 3: Windows PowerShell (without Maven)

```powershell
# Compile
$files = Get-ChildItem src\main\java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if (Test-Path out) { Remove-Item out -Recurse -Force }
New-Item -ItemType Directory -Path out | Out-Null
javac -d out $files

# Run
java -cp out com.cusat.MainApp
```

## Web GUI

### Start the web server:

```bash
mvn exec:java@run-web-gui
```

### Open in your browser:

```
http://localhost:8085
```

### How it works:

1. `WebGuiServer` starts a local HTTP server on port `8085`
2. `index.html` serves a browser form for IPv4 input
3. `app.js` sends the entered IPv4 address to `/api/scan`
4. The `/api/scan` handler creates a `ScanRequest`
5. `ScanOrchestrator` runs the existing scanner pipeline
6. The server returns scan results as JSON
7. The webpage renders ports, findings, and the timeline

> **Note:** The webpage is only a user interface over the same Java scanning logic, not a separate scanner.

## Testing

### Run all tests:

```bash
mvn test
```

### Test coverage:

- ✅ IPv4-only input validation
- ✅ Risk scoring behavior
- ✅ Report filename format
- ✅ Local open-port detection
- ✅ Network utility behavior

## Current Limitations

- TCP connect scanning only (no UDP)
- No authenticated checks
- No CVE mapping
- No PDF or DOCX report export
- No persistent database or dashboard backend
- Hostnames and URLs are intentionally blocked

## Roadmap

### Phase 2 Priorities

- [ ] Externalize rules into JSON or configuration files
- [ ] Integrate banner grabbing and deeper service fingerprinting
- [ ] Add structured JSON output as reporting source of truth
- [ ] Generate PDF and DOCX reports from structured result data
- [ ] Add explicit authorization safeguards for non-local scanning
- [ ] Expand web interface into a fuller dashboard

## Ethical Constraints

> **⚠️ IMPORTANT:** Use this tool only on systems and networks you own or are explicitly authorized to assess.

### Project Constraints:

- ❌ No exploit modules
- ❌ No persistence features
- ❌ No password spraying or brute force features
- ❌ No stealth/evasion additions
- ❌ No default wide-area scanning behavior
- ✅ Explicit user-visible warnings and authorization expectations

## License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2026 Aditya Kumar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Author

**Aditya Kumar**

- GitHub: [@AdityaKumarExplorer](https://github.com/AdityaKumarExplorer)
- Project: [CuSAT](https://github.com/AdityaKumarExplorer/CuSAT)

## Disclaimer

CuSAT is a **defensive learning project** for authorized exposure assessment and reporting only.

The author is not responsible for any misuse of this tool. Users are solely responsible for complying with all applicable laws and obtaining proper authorization before using this tool on any network or system.

---

*Made for educational and defensive security purposes.*
