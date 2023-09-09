const fs = require('fs');
const detektReportPath = "build/reports/detekt/detekt.html";
if (fs.existsSync(detektReportPath)) {
    const reportContent = fs.readFileSync(detektReportPath, 'utf8');
    markdown(reportContent);
} else {
    markdown("❌ Detekt report not found!");
}