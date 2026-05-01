const form = document.getElementById("scan-form");
const statusBox = document.getElementById("status");
const resultSection = document.getElementById("result");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const ip = document.getElementById("ip").value.trim();
    statusBox.textContent = "Running scan...";
    resultSection.classList.add("hidden");

    try {
        const response = await fetch(`/api/scan?ip=${encodeURIComponent(ip)}`);
        const payload = await response.json();

        if (!response.ok) {
            throw new Error(payload.error || "Scan failed.");
        }

        renderResult(payload);
        statusBox.textContent = "Scan completed.";
        resultSection.classList.remove("hidden");
    } catch (error) {
        statusBox.textContent = error.message;
    }
});

function renderResult(payload) {
    document.getElementById("target-value").textContent = payload.target;
    document.getElementById("reachable-value").textContent = payload.reachable ? "Yes" : "No";
    document.getElementById("duration-value").textContent = `${payload.durationMs} ms`;
    document.getElementById("risk-value").textContent = payload.overallRisk;

    fillList("ports-list", payload.ports
        .filter((port) => port.open)
        .map((port) => `${port.port}/tcp - ${port.status}${port.serviceName && port.serviceName !== "unknown" ? ` (${port.serviceName})` : ""}`),
        "No open ports detected.");

    fillList("findings-list", payload.findings
        .map((finding) => `${finding.severity} - ${finding.description} [${finding.portOrService}]`),
        "No notable findings.");

    fillList("timeline-list", payload.timeline, "No timeline events recorded.");
}

function fillList(id, items, fallback) {
    const list = document.getElementById(id);
    list.innerHTML = "";

    const values = items.length > 0 ? items : [fallback];
    values.forEach((value) => {
        const li = document.createElement("li");
        li.textContent = value;
        list.appendChild(li);
    });
}
