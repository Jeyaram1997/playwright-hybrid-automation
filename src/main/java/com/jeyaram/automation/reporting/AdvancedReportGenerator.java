package com.jeyaram.automation.reporting;

import com.itextpdf.html2pdf.HtmlConverter;
import com.jeyaram.automation.config.ConfigManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Advanced Report Generator for multiple formats (PDF, Excel, CSV)
 * Author: Jeyaram K
 */
public class AdvancedReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedReportGenerator.class);
    private final ConfigManager configManager;
    private final String reportsDir;
    
    public AdvancedReportGenerator() {
        this.configManager = ConfigManager.getInstance();
        this.reportsDir = configManager.getProperty("reports.output.dir", "target/reports");
        createReportsDirectory();
    }
    
    /**
     * Generate PDF report from HTML content
     */
    public String generatePDFReport(String htmlContent, String reportName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.pdf", reportName, timestamp);
            String filePath = Paths.get(reportsDir, "pdf", fileName).toString();
            
            createDirectoryIfNotExists(Paths.get(reportsDir, "pdf"));
            
            // Create PDF from HTML using FileOutputStream
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                HtmlConverter.convertToPdf(htmlContent, fos);
            }
            
            logger.info("PDF report generated: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Generate Excel report from test results
     */
    public String generateExcelReport(List<Map<String, Object>> testResults, String reportName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.xlsx", reportName, timestamp);
            String filePath = Paths.get(reportsDir, "excel", fileName).toString();
            
            createDirectoryIfNotExists(Paths.get(reportsDir, "excel"));
            
            Workbook workbook = new XSSFWorkbook();
            
            // Create summary sheet
            createSummarySheet(workbook, testResults);
            
            // Create detailed results sheet
            createDetailedResultsSheet(workbook, testResults);
            
            // Create charts sheet (if needed)
            createChartsSheet(workbook, testResults);
            
            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            
            workbook.close();
            
            logger.info("Excel report generated: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Error generating Excel report: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Generate CSV report from test results
     */
    public String generateCSVReport(List<Map<String, Object>> testResults, String reportName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.csv", reportName, timestamp);
            String filePath = Paths.get(reportsDir, "csv", fileName).toString();
            
            createDirectoryIfNotExists(Paths.get(reportsDir, "csv"));
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                // Write header
                writer.println("Test Name,Status,Duration (ms),Start Time,End Time,Environment,Test Type,Error Message");
                
                // Write test results
                for (Map<String, Object> result : testResults) {
                    StringBuilder line = new StringBuilder();
                    line.append(escapeCSV(getString(result, "testName"))).append(",");
                    line.append(escapeCSV(getString(result, "status"))).append(",");
                    line.append(getString(result, "duration")).append(",");
                    line.append(escapeCSV(getString(result, "startTime"))).append(",");
                    line.append(escapeCSV(getString(result, "endTime"))).append(",");
                    line.append(escapeCSV(getString(result, "environment"))).append(",");
                    line.append(escapeCSV(getString(result, "testType"))).append(",");
                    line.append(escapeCSV(getString(result, "errorMessage")));
                    
                    writer.println(line.toString());
                }
            }
            
            logger.info("CSV report generated: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Error generating CSV report: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Generate comprehensive test report in all formats
     */
    public ReportGenerationResult generateComprehensiveReport(List<Map<String, Object>> testResults, 
                                                            String htmlContent, String reportName) {
        ReportGenerationResult result = new ReportGenerationResult();
        
        // Generate PDF report
        String pdfPath = generatePDFReport(htmlContent, reportName);
        result.setPdfPath(pdfPath);
        
        // Generate Excel report
        String excelPath = generateExcelReport(testResults, reportName);
        result.setExcelPath(excelPath);
        
        // Generate CSV report
        String csvPath = generateCSVReport(testResults, reportName);
        result.setCsvPath(csvPath);
        
        // Generate summary HTML
        String summaryHtml = generateSummaryHTML(testResults, reportName);
        result.setHtmlPath(summaryHtml);
        
        result.setSuccess(pdfPath != null || excelPath != null || csvPath != null);
        
        return result;
    }
    
    /**
     * Generate performance report in multiple formats
     */
    public ReportGenerationResult generatePerformanceReport(Map<String, Object> performanceData, String reportName) {
        ReportGenerationResult result = new ReportGenerationResult();
        
        try {
            // Generate performance HTML
            String htmlContent = generatePerformanceHTML(performanceData);
            String htmlPath = saveHTMLReport(htmlContent, reportName + "_performance");
            result.setHtmlPath(htmlPath);
            
            // Generate PDF from HTML
            String pdfPath = generatePDFReport(htmlContent, reportName + "_performance");
            result.setPdfPath(pdfPath);
            
            // Generate performance CSV
            String csvPath = generatePerformanceCSV(performanceData, reportName + "_performance");
            result.setCsvPath(csvPath);
            
            result.setSuccess(true);
            
        } catch (Exception e) {
            logger.error("Error generating performance report: {}", e.getMessage(), e);
            result.setSuccess(false);
        }
        
        return result;
    }
    
    private void createSummarySheet(Workbook workbook, List<Map<String, Object>> testResults) {
        Sheet sheet = workbook.createSheet("Summary");
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        // Summary statistics
        int passed = (int) testResults.stream().filter(r -> "PASSED".equals(r.get("status"))).count();
        int failed = (int) testResults.stream().filter(r -> "FAILED".equals(r.get("status"))).count();
        int skipped = (int) testResults.stream().filter(r -> "SKIPPED".equals(r.get("status"))).count();
        
        // Create summary rows
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("Test Execution Summary");
        row0.getCell(0).setCellStyle(headerStyle);
        
        Row row1 = sheet.createRow(2);
        row1.createCell(0).setCellValue("Total Tests:");
        row1.createCell(1).setCellValue(testResults.size());
        
        Row row2 = sheet.createRow(3);
        row2.createCell(0).setCellValue("Passed:");
        row2.createCell(1).setCellValue(passed);
        
        Row row3 = sheet.createRow(4);
        row3.createCell(0).setCellValue("Failed:");
        row3.createCell(1).setCellValue(failed);
        
        Row row4 = sheet.createRow(5);
        row4.createCell(0).setCellValue("Skipped:");
        row4.createCell(1).setCellValue(skipped);
        
        Row row5 = sheet.createRow(6);
        row5.createCell(0).setCellValue("Success Rate:");
        double successRate = testResults.isEmpty() ? 0 : (double) passed / testResults.size() * 100;
        row5.createCell(1).setCellValue(String.format("%.2f%%", successRate));
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void createDetailedResultsSheet(Workbook workbook, List<Map<String, Object>> testResults) {
        Sheet sheet = workbook.createSheet("Detailed Results");
        
        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Test Name", "Status", "Duration (ms)", "Start Time", "End Time", "Environment", "Test Type", "Error Message"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Add test result data
        int rowNum = 1;
        for (Map<String, Object> result : testResults) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(getString(result, "testName"));
            row.createCell(1).setCellValue(getString(result, "status"));
            row.createCell(2).setCellValue(getDouble(result, "duration"));
            row.createCell(3).setCellValue(getString(result, "startTime"));
            row.createCell(4).setCellValue(getString(result, "endTime"));
            row.createCell(5).setCellValue(getString(result, "environment"));
            row.createCell(6).setCellValue(getString(result, "testType"));
            row.createCell(7).setCellValue(getString(result, "errorMessage"));
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createChartsSheet(Workbook workbook, List<Map<String, Object>> testResults) {
        Sheet sheet = workbook.createSheet("Charts");
        
        // This is a placeholder for chart creation
        // In a full implementation, you would use Apache POI's chart APIs
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Charts would be generated here using Apache POI Chart APIs");
    }
    
    private String generateSummaryHTML(List<Map<String, Object>> testResults, String reportName) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head>");
            html.append("<title>").append(reportName).append(" - Test Report</title>");
            html.append("<style>");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
            html.append("table { border-collapse: collapse; width: 100%; }");
            html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            html.append("th { background-color: #f2f2f2; }");
            html.append(".passed { color: green; font-weight: bold; }");
            html.append(".failed { color: red; font-weight: bold; }");
            html.append(".skipped { color: orange; font-weight: bold; }");
            html.append("</style></head><body>");
            
            html.append("<h1>").append(reportName).append(" - Test Execution Report</h1>");
            html.append("<p>Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</p>");
            
            // Summary statistics
            int passed = (int) testResults.stream().filter(r -> "PASSED".equals(r.get("status"))).count();
            int failed = (int) testResults.stream().filter(r -> "FAILED".equals(r.get("status"))).count();
            int skipped = (int) testResults.stream().filter(r -> "SKIPPED".equals(r.get("status"))).count();
            
            html.append("<h2>Summary</h2>");
            html.append("<table>");
            html.append("<tr><th>Metric</th><th>Value</th></tr>");
            html.append("<tr><td>Total Tests</td><td>").append(testResults.size()).append("</td></tr>");
            html.append("<tr><td>Passed</td><td class='passed'>").append(passed).append("</td></tr>");
            html.append("<tr><td>Failed</td><td class='failed'>").append(failed).append("</td></tr>");
            html.append("<tr><td>Skipped</td><td class='skipped'>").append(skipped).append("</td></tr>");
            
            double successRate = testResults.isEmpty() ? 0 : (double) passed / testResults.size() * 100;
            html.append("<tr><td>Success Rate</td><td>").append(String.format("%.2f%%", successRate)).append("</td></tr>");
            html.append("</table>");
            
            html.append("</body></html>");
            
            return saveHTMLReport(html.toString(), reportName + "_summary");
            
        } catch (Exception e) {
            logger.error("Error generating summary HTML: {}", e.getMessage(), e);
            return null;
        }
    }
    
    private String generatePerformanceHTML(Map<String, Object> performanceData) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<title>Performance Test Report</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        html.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append(".metric { font-weight: bold; }");
        html.append("</style></head><body>");
        
        html.append("<h1>Performance Test Report</h1>");
        html.append("<p>Generated on: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("</p>");
        
        html.append("<h2>Performance Metrics</h2>");
        html.append("<table>");
        html.append("<tr><th>Metric</th><th>Value</th></tr>");
        
        for (Map.Entry<String, Object> entry : performanceData.entrySet()) {
            html.append("<tr>");
            html.append("<td class='metric'>").append(entry.getKey()).append("</td>");
            html.append("<td>").append(entry.getValue()).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</table>");
        html.append("</body></html>");
        
        return html.toString();
    }
    
    private String generatePerformanceCSV(Map<String, Object> performanceData, String reportName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.csv", reportName, timestamp);
            String filePath = Paths.get(reportsDir, "csv", fileName).toString();
            
            createDirectoryIfNotExists(Paths.get(reportsDir, "csv"));
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println("Metric,Value");
                
                for (Map.Entry<String, Object> entry : performanceData.entrySet()) {
                    writer.println(escapeCSV(entry.getKey()) + "," + escapeCSV(entry.getValue().toString()));
                }
            }
            
            logger.info("Performance CSV generated: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Error generating performance CSV: {}", e.getMessage(), e);
            return null;
        }
    }
    
    private String saveHTMLReport(String htmlContent, String reportName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.html", reportName, timestamp);
            String filePath = Paths.get(reportsDir, "html", fileName).toString();
            
            createDirectoryIfNotExists(Paths.get(reportsDir, "html"));
            
            Files.write(Paths.get(filePath), htmlContent.getBytes());
            
            logger.info("HTML report saved: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Error saving HTML report: {}", e.getMessage(), e);
            return null;
        }
    }
    
    private void createReportsDirectory() {
        try {
            Path reportsPath = Paths.get(reportsDir);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
            }
        } catch (Exception e) {
            logger.error("Error creating reports directory: {}", e.getMessage(), e);
        }
    }
    
    private void createDirectoryIfNotExists(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            logger.error("Error creating directory {}: {}", path, e.getMessage(), e);
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    private double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Result class for report generation
     */
    public static class ReportGenerationResult {
        private boolean success;
        private String pdfPath;
        private String excelPath;
        private String csvPath;
        private String htmlPath;
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getPdfPath() { return pdfPath; }
        public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
        
        public String getExcelPath() { return excelPath; }
        public void setExcelPath(String excelPath) { this.excelPath = excelPath; }
        
        public String getCsvPath() { return csvPath; }
        public void setCsvPath(String csvPath) { this.csvPath = csvPath; }
        
        public String getHtmlPath() { return htmlPath; }
        public void setHtmlPath(String htmlPath) { this.htmlPath = htmlPath; }
    }
}
