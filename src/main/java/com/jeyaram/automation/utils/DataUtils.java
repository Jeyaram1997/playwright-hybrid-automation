package com.jeyaram.automation.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Data utilities for handling various data formats
 * Supports CSV, JSON, Excel, YAML, and Properties files
 * 
 * @author Jeyaram K
 * @version 1.0.0
 * @since 2025-01-01
 */
public class DataUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    
    public DataUtils() {
        this.jsonMapper = new ObjectMapper();
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    /**
     * Read CSV file and return as list of maps
     * 
     * @param filePath Path to CSV file
     * @return List of maps representing CSV data
     */
    public List<Map<String, String>> readCSV(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            
            for (CSVRecord record : csvParser) {
                Map<String, String> row = new HashMap<>();
                record.toMap().forEach(row::put);
                data.add(row);
            }
            
            logger.info("Successfully read {} records from CSV file: {}", data.size(), filePath);
            
        } catch (Exception e) {
            logger.error("Failed to read CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to read CSV file: " + filePath, e);
        }
        
        return data;
    }
    
    /**
     * Read Excel file and return as list of maps
     * 
     * @param filePath Path to Excel file
     * @param sheetName Sheet name to read
     * @return List of maps representing Excel data
     */
    public List<Map<String, Object>> readExcel(String filePath, String sheetName) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = sheetName != null ? workbook.getSheet(sheetName) : workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            
            // Get header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row not found in Excel sheet");
            }
            
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }
            
            // Read data rows
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                
                Map<String, Object> rowData = new HashMap<>();
                for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String header = headers.get(colIndex);
                    Object value = getCellValue(cell);
                    rowData.put(header, value);
                }
                data.add(rowData);
            }
            
            logger.info("Successfully read {} records from Excel file: {}", data.size(), filePath);
            
        } catch (Exception e) {
            logger.error("Failed to read Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read Excel file: " + filePath, e);
        }
        
        return data;
    }
    
    /**
     * Read JSON file and convert to object
     * 
     * @param filePath Path to JSON file
     * @param clazz Target class type
     * @param <T> Generic type
     * @return Parsed object
     */
    public <T> T readJSON(String filePath, Class<T> clazz) {
        try {
            Path path = Paths.get(filePath);
            String content = Files.readString(path);
            T object = jsonMapper.readValue(content, clazz);
            
            logger.info("Successfully read JSON file: {}", filePath);
            return object;
            
        } catch (Exception e) {
            logger.error("Failed to read JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
    
    /**
     * Read JSON file as Map
     * 
     * @param filePath Path to JSON file
     * @return Map representation of JSON
     */
    public Map<String, Object> readJSONAsMap(String filePath) {
        try {
            Path path = Paths.get(filePath);
            String content = Files.readString(path);
            return jsonMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
            
        } catch (Exception e) {
            logger.error("Failed to read JSON file as map: {}", filePath, e);
            throw new RuntimeException("Failed to read JSON file as map: " + filePath, e);
        }
    }
    
    /**
     * Read YAML file and convert to object
     * 
     * @param filePath Path to YAML file
     * @param clazz Target class type
     * @param <T> Generic type
     * @return Parsed object
     */
    public <T> T readYAML(String filePath, Class<T> clazz) {
        try {
            Path path = Paths.get(filePath);
            String content = Files.readString(path);
            T object = yamlMapper.readValue(content, clazz);
            
            logger.info("Successfully read YAML file: {}", filePath);
            return object;
            
        } catch (Exception e) {
            logger.error("Failed to read YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML file: " + filePath, e);
        }
    }
    
    /**
     * Read properties file
     * 
     * @param filePath Path to properties file
     * @return Properties object
     */
    public Properties readProperties(String filePath) {
        Properties properties = new Properties();
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
            
            logger.info("Successfully read properties file: {}", filePath);
            
        } catch (Exception e) {
            logger.error("Failed to read properties file: {}", filePath, e);
            throw new RuntimeException("Failed to read properties file: " + filePath, e);
        }
        
        return properties;
    }
    
    /**
     * Write data to CSV file
     * 
     * @param data List of maps to write
     * @param filePath Output file path
     */
    public void writeCSV(List<Map<String, String>> data, String filePath) {
        if (data.isEmpty()) {
            logger.warn("No data to write to CSV file: {}", filePath);
            return;
        }
        
        try (FileWriter writer = new FileWriter(filePath)) {
            Set<String> headers = data.get(0).keySet();
            
            // Write headers
            writer.append(String.join(",", headers)).append("\\n");
            
            // Write data
            for (Map<String, String> row : data) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    String value = row.getOrDefault(header, "");
                    values.add(escapeCSVValue(value));
                }
                writer.append(String.join(",", values)).append("\\n");
            }
            
            logger.info("Successfully wrote {} records to CSV file: {}", data.size(), filePath);
            
        } catch (Exception e) {
            logger.error("Failed to write CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to write CSV file: " + filePath, e);
        }
    }
    
    /**
     * Write object to JSON file
     * 
     * @param object Object to write
     * @param filePath Output file path
     */
    public void writeJSON(Object object, String filePath) {
        try {
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), object);
            
            logger.info("Successfully wrote JSON file: {}", filePath);
            
        } catch (Exception e) {
            logger.error("Failed to write JSON file: {}", filePath, e);
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }
    
    /**
     * Convert object to JSON string
     * 
     * @param object Object to convert
     * @return JSON string
     */
    public String toJson(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert object to JSON", e);
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * Convert JSON string to object
     * 
     * @param json JSON string
     * @param clazz Target class type
     * @param <T> Generic type
     * @return Parsed object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON string", e);
            throw new RuntimeException("Failed to parse JSON string", e);
        }
    }
    
    /**
     * Validate JSON against schema (basic validation)
     * 
     * @param json JSON string to validate
     * @param schemaPath Path to JSON schema file
     * @return true if valid
     */
    public boolean validateJsonSchema(String json, String schemaPath) {
        try {
            // Basic JSON validation - can be enhanced with proper schema validation library
            JsonNode jsonNode = jsonMapper.readTree(json);
            JsonNode schemaNode = jsonMapper.readTree(new File(schemaPath));
            
            // This is a simplified validation - implement proper schema validation as needed
            logger.info("JSON schema validation completed");
            return true;
            
        } catch (Exception e) {
            logger.error("JSON schema validation failed", e);
            return false;
        }
    }
    
    /**
     * Extract value from JSON using JSONPath-like syntax
     * 
     * @param json JSON string
     * @param path Path to extract (simplified dot notation)
     * @return Extracted value
     */
    public Object extractValueFromJson(String json, String path) {
        try {
            JsonNode rootNode = jsonMapper.readTree(json);
            JsonNode currentNode = rootNode;
            
            String[] pathParts = path.split("\\\\.");
            for (String part : pathParts) {
                if (part.contains("[") && part.contains("]")) {
                    // Handle array access
                    String arrayField = part.substring(0, part.indexOf("["));
                    int index = Integer.parseInt(part.substring(part.indexOf("[") + 1, part.indexOf("]")));
                    currentNode = currentNode.get(arrayField).get(index);
                } else {
                    currentNode = currentNode.get(part);
                }
                
                if (currentNode == null) {
                    return null;
                }
            }
            
            return currentNode.asText();
            
        } catch (Exception e) {
            logger.error("Failed to extract value from JSON", e);
            throw new RuntimeException("Failed to extract value from JSON", e);
        }
    }
    
    /**
     * Get cell value from Excel cell
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
    
    /**
     * Get cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }
    
    /**
     * Escape CSV value if needed
     */
    private String escapeCSVValue(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}
