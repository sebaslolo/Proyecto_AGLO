(() => {
    "use strict";

    const palette = [
        "#006f68",
        "#2e8f84",
        "#72bfb2",
        "#e6a23c",
        "#4f73c8",
        "#8a65b7",
        "#d96857"
    ];

    const normaliseSeries = (series) => {
        const labels = Array.isArray(series?.labels) ? series.labels : [];
        const values = Array.isArray(series?.values) ? series.values : [];

        return labels.map((label, index) => ({
            label: String(label ?? ""),
            value: Number(values[index])
        })).filter((entry) => entry.label && Number.isFinite(entry.value));
    };

    const canRender = (canvas, entries) => canvas && entries.length > 0 && typeof window.Chart === "function";

    const destroyExistingChart = (canvas) => {
        if (typeof window.Chart?.getChart === "function") {
            window.Chart.getChart(canvas)?.destroy();
        }
    };

    const renderMonthlyChart = (canvas, entries) => {
        if (!canRender(canvas, entries)) {
            return;
        }

        destroyExistingChart(canvas);
        new window.Chart(canvas, {
            type: "line",
            data: {
                labels: entries.map((entry) => entry.label),
                datasets: [{
                    data: entries.map((entry) => entry.value),
                    borderColor: "#006f68",
                    backgroundColor: "rgba(0, 111, 104, .16)",
                    borderWidth: 3,
                    fill: true,
                    tension: .36,
                    pointBackgroundColor: "#ffffff",
                    pointBorderColor: "#006f68",
                    pointBorderWidth: 2,
                    pointHoverRadius: 5,
                    pointRadius: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {intersect: false, mode: "index"},
                plugins: {
                    legend: {display: false},
                    tooltip: {
                        displayColors: false,
                        padding: 10,
                        callbacks: {
                            label: (context) => `${context.parsed.y ?? 0}`
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {display: false},
                        ticks: {color: "#617471", font: {size: 11, weight: "600"}}
                    },
                    y: {
                        beginAtZero: true,
                        border: {display: false},
                        grid: {color: "rgba(91, 124, 119, .14)"},
                        ticks: {
                            color: "#617471",
                            precision: 0,
                            font: {size: 11, weight: "600"}
                        }
                    }
                }
            }
        });
    };

    const renderStatusChart = (canvas, entries) => {
        if (!canRender(canvas, entries)) {
            return;
        }

        destroyExistingChart(canvas);
        new window.Chart(canvas, {
            type: "doughnut",
            data: {
                labels: entries.map((entry) => entry.label),
                datasets: [{
                    data: entries.map((entry) => entry.value),
                    backgroundColor: entries.map((_, index) => palette[index % palette.length]),
                    borderColor: "#f8fcfb",
                    borderWidth: 4,
                    hoverOffset: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: "67%",
                plugins: {
                    legend: {
                        position: "bottom",
                        labels: {
                            boxWidth: 10,
                            boxHeight: 10,
                            padding: 13,
                            color: "#405553",
                            font: {size: 11, weight: "600"}
                        }
                    },
                    tooltip: {
                        padding: 10,
                        callbacks: {
                            label: (context) => `${context.label}: ${context.parsed ?? 0}`
                        }
                    }
                }
            }
        });
    };

    document.addEventListener("DOMContentLoaded", () => {
        const dashboardData = window.agloDashboardData;
        if (!dashboardData || typeof window.Chart !== "function") {
            return;
        }

        renderMonthlyChart(
            document.getElementById("reservationsMonthlyChart"),
            normaliseSeries(dashboardData.monthly)
        );
        renderStatusChart(
            document.getElementById("reservationsStatusChart"),
            normaliseSeries(dashboardData.status)
        );
    });
})();
