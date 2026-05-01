# CuSAT Progress Context Prompt

Use the following prompt as context when continuing work on the project:

```text
Project: CuSAT (Custom Security Assessment Tool)

Current state:
- Java-based defensive security assessment prototype
- CLI supports only help, exit, and IPv4 addresses
- Core flow: MainApp -> ScanRequest/Target -> ScanOrchestrator -> HostDiscovery -> PortScanner -> RuleEngine -> RiskScorer -> ReportGenerator
- PortScanner uses bounded concurrency and a shared default port list
- Reports are exported to output/reports using the name format Report(CuSAT)[IP][date][time].txt
- Web GUI exists through com.cusat.web.WebGuiServer and src/main/resources/web assets
- Tests currently cover input validation, risk scoring, report naming, port scanning, and network utility behavior
- Maven project metadata and IDE config files are present in CuSAT - Copy

Important design constraints:
- Keep the project non-exploitative
- Support authorized defensive scanning and reporting only
- Avoid exploit delivery, persistence, credential attacks, or stealth features
- Prefer clear reporting, validation, and auditability

Phase 2 direction:
- externalize rules
- improve service fingerprinting
- create structured JSON report output
- add PDF/DOCX reporting
- strengthen authorization safeguards for external/private-network scanning
- grow the web interface into a fuller dashboard
```
