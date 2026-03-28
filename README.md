# 🛡️ CuSAT – Custom Security Assessment Tool

**Tagline:**
Java-based rule-driven network exposure assessment tool for ethical, local system security analysis.

---

## 📌 Overview

CuSAT is a **Java-based defensive cybersecurity tool** designed to analyze **network exposure on the local machine**.

This is a **Self-Scan Edition**, meaning:

* It scans **only the user’s own system (127.0.0.1)**
* It avoids intrusive or exploitative techniques
* It focuses on **learning and defensive awareness**

---

## 🎯 Features (Phase 1)

* TCP Connect Port Scanning
* Host Reachability Detection
* Rule-Based Risk Assessment
* Console Report Generation
* Text File Report Export
* Timeline Logging
* Interactive CLI Interface

---

## 🧩 Architecture

```
User Input
   ↓
ScanRequest
   ↓
ScanOrchestrator
   ├── HostDiscovery
   ├── PortScanner
   ├── RuleEngine → RiskScorer
   └── ReportGenerator → TextReportWriter
```

---

## 🔍 Rule Engine

### Service Exposure

| Port   | Service    | Risk   |
| ------ | ---------- | ------ |
| 22     | SSH        | MEDIUM |
| 21     | FTP        | HIGH   |
| 80/443 | HTTP/HTTPS | LOW    |

### High-Risk Services

| Port | Service |
| ---- | ------- |
| 445  | SMB     |
| 3389 | RDP     |
| 3306 | MySQL   |

---

## ▶️ How to Run

### Compile

```
javac -d out $(find src -name "*.java")
```

### Run

```
java -cp out com.cusat.MainApp
```

---

## 💻 Usage

```
cusat> 127.0.0.1
cusat> help
cusat> exit
```

⚠️ Only **127.0.0.1 (localhost)** is supported in this version.

---

## 📊 Sample Output

```
================ CSAT SCAN REPORT ================
Target:        127.0.0.1
Reachable:     Yes
Duration:      102ms
Overall Risk:  Low

Open Ports:    1 / 12
--------------------------------------------------
  Open ports details:
  445    open

Findings Summary:
  High SMB exposed (445/tcp)
==================================================
```

---

## 📁 Output Files

Reports are saved in:

```
/output/reports/
```

---

## ⚠️ Ethical Use

* ✅ Scan your own system only
* ❌ Do not scan external systems

---

## 🚀 Phase 1 Scope

### Completed

* Port scanning
* Rule engine
* CLI interface
* Report generation

### Not Included

* UDP scanning
* Web UI
* PDF reports
* Advanced detection

---

## 🔮 Future Scope

* Multithreading
* TLS inspection
* Web dashboard
* JSON/PDF reports

---

## 👨‍💻 Author

Aditya Kumar
Computer Science Student

---

## 🧠 Learning Outcome

* Java Networking
* Security Analysis
* System Design
* Ethical Cybersecurity

---

**CuSAT – Self Awareness Before Exploitation**
