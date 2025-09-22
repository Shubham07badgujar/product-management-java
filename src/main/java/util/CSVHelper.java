package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.ProductDao;
import dao.ProductDaoImpl;
import exception.DatabaseOperationException;
import model.Product;

public class CSVHelper {

    private static final String CSV_DIRECTORY = "data";
    private static final String CSV_FILENAME = "products.csv";
    private static final String REPORTS_DIRECTORY = "reports";
    private static final String CSV_HEADER = "ID,Name,Price,Quantity,Created_At,Updated_At";
    private static final String CSV_SEPARATOR = ",";

    private static final Path CSV_FILE_PATH = Paths.get(CSV_DIRECTORY, CSV_FILENAME);
    private static final Path REPORTS_DIR_PATH = Paths.get(REPORTS_DIRECTORY);

    private final ProductDao productDao;

    public CSVHelper() {
        this.productDao = new ProductDaoImpl();
        initializeDirectories();
        initializeCSVFile();
    }

    private void initializeDirectories() {
        try {
            if (!Files.exists(Paths.get(CSV_DIRECTORY))) {
                Files.createDirectories(Paths.get(CSV_DIRECTORY));
                System.out.println("Created data directory for CSV files");
            }

            if (!Files.exists(REPORTS_DIR_PATH)) {
                Files.createDirectories(REPORTS_DIR_PATH);
                System.out.println("Created reports directory");
            }
        } catch (IOException e) {
            System.err.println("❌ Error creating directories: " + e.getMessage());
        }
    }

    /**
     * Initialize CSV file with header if it doesn't exist
     */
    private void initializeCSVFile() {
        try {
            if (!Files.exists(CSV_FILE_PATH)) {
                Files.write(CSV_FILE_PATH, CSV_HEADER.getBytes());
                System.out.println("📄 Initialized CSV file with headers");
            }
        } catch (IOException e) {
            System.err.println("❌ Error initializing CSV file: " + e.getMessage());
        }
    }

    /**
     * Add a new product to both CSV file and database
     * Ensures data consistency between both sources
     * 
     * @param product Product to be added
     * @return true if successful, false otherwise
     */
    public boolean addProductToCSVAndDB(Product product) {
        if (product == null) {
            System.err.println("❌ Cannot add null product");
            return false;
        }

        try {
            // First, add to database
            boolean dbSuccess = productDao.create(product);

            if (dbSuccess) {
                // If database operation successful, add to CSV
                String csvLine = productToCSVLine(product);
                Files.write(CSV_FILE_PATH, (System.lineSeparator() + csvLine).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                System.out.println("✅ Product added to both database and CSV successfully");
                return true;
            } else {
                System.err.println("❌ Failed to add product to database");
                return false;
            }

        } catch (DatabaseOperationException | exception.ProductValidationException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("❌ CSV file error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sync all data from database to CSV file
     * Rebuilds the entire CSV file from current database state
     * 
     * @return true if sync successful, false otherwise
     */
    public boolean syncDatabaseToCSV() {
        try {
            System.out.println("🔄 Starting database to CSV synchronization...");

            // Get all products from database
            List<Product> products = productDao.findAll();

            // Rebuild CSV file
            List<String> csvLines = new ArrayList<>();
            csvLines.add(CSV_HEADER);

            for (Product product : products) {
                csvLines.add(productToCSVLine(product));
            }

            // Write all lines to CSV file
            Files.write(CSV_FILE_PATH, csvLines, StandardCharsets.UTF_8);

            System.out.println("✅ Successfully synced " + products.size() + " products to CSV");
            return true;

        } catch (DatabaseOperationException e) {
            System.err.println("❌ Database error during sync: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("❌ File error during sync: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetch all products from CSV file
     * 
     * @return List of products from CSV, empty list if error
     */
    public List<Product> fetchProductsFromCSV() {
        List<Product> products = new ArrayList<>();

        try {
            if (!Files.exists(CSV_FILE_PATH)) {
                System.out.println("📄 CSV file not found, returning empty list");
                return products;
            }

            List<String> lines = Files.readAllLines(CSV_FILE_PATH);

            // Skip header line (index 0)
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    Product product = csvLineToProduct(line);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }

            System.out.println("📖 Fetched " + products.size() + " products from CSV");

        } catch (IOException e) {
            System.err.println("❌ Error reading CSV file: " + e.getMessage());
        }

        return products;
    }

    /**
     * Generate a downloadable CSV report with timestamp
     * Creates a new CSV file in reports directory with current database data
     * 
     * @return Path to generated report file, null if failed
     */
    public Path generateDownloadableReport() {
        try {
            // Create timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportFilename = "product_report_" + timestamp + ".csv";
            Path reportPath = REPORTS_DIR_PATH.resolve(reportFilename);

            System.out.println("📊 Generating product report...");

            // Get all products from database
            List<Product> products = productDao.findAll();

            // Create report with header and data
            List<String> reportLines = new ArrayList<>();
            reportLines.add("# Product Management System Report");
            reportLines.add("# Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            reportLines.add("# Total Products: " + products.size());
            reportLines.add("");
            reportLines.add(CSV_HEADER);

            for (Product product : products) {
                reportLines.add(productToCSVLine(product));
            }

            // Write report to file
            Files.write(reportPath, reportLines, StandardCharsets.UTF_8);

            System.out.println("✅ Report generated successfully!");
            System.out.println("📂 Report location: " + reportPath.toAbsolutePath());
            System.out.println("📊 Total products in report: " + products.size());

            return reportPath;

        } catch (DatabaseOperationException e) {
            System.err.println("❌ Database error during report generation: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("❌ File error during report generation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convert Product object to CSV line format
     * 
     * @param product Product to convert
     * @return CSV formatted string
     */
    private String productToCSVLine(Product product) {
        return String.join(CSV_SEPARATOR,
                String.valueOf(product.getId()),
                escapeCSVField(product.getName()),
                String.valueOf(product.getPrice()),
                String.valueOf(product.getQuantity()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * Convert CSV line to Product object
     * 
     * @param csvLine CSV line to parse
     * @return Product object or null if parsing fails
     */
    private Product csvLineToProduct(String csvLine) {
        try {
            String[] fields = csvLine.split(CSV_SEPARATOR);
            if (fields.length >= 4) {
                int id = Integer.parseInt(fields[0].trim());
                String name = unescapeCSVField(fields[1].trim());
                double price = Double.parseDouble(fields[2].trim());
                int quantity = Integer.parseInt(fields[3].trim());

                return new Product(id, name, price, quantity);
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Error parsing CSV line: " + csvLine);
        }
        return null;
    }

    /**
     * Escape CSV field (handle commas and quotes)
     */
    private String escapeCSVField(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Unescape CSV field
     */
    private String unescapeCSVField(String field) {
        if (field.startsWith("\"") && field.endsWith("\"")) {
            return field.substring(1, field.length() - 1).replace("\"\"", "\"");
        }
        return field;
    }

    /**
     * Get CSV file statistics
     * 
     * @return Map with file statistics
     */
    public Map<String, Object> getCSVStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            if (Files.exists(CSV_FILE_PATH)) {
                long fileSize = Files.size(CSV_FILE_PATH);
                List<String> lines = Files.readAllLines(CSV_FILE_PATH);
                int recordCount = Math.max(0, lines.size() - 1); // Exclude header

                stats.put("file_exists", true);
                stats.put("file_size_bytes", fileSize);
                stats.put("record_count", recordCount);
                stats.put("last_modified", Files.getLastModifiedTime(CSV_FILE_PATH).toString());
            } else {
                stats.put("file_exists", false);
                stats.put("record_count", 0);
            }
        } catch (IOException e) {
            stats.put("error", e.getMessage());
        }

        return stats;
    }
}