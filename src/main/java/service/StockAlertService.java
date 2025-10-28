package service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.MessagingException;
import model.Product;

/**
 * StockAlertService handles low-stock monitoring and automated alerts
 * Features:
 * - Checks for low-stock products
 * - Sends email notifications to admin
 * - Automated daily monitoring with scheduler
 * - Manual stock checks
 */
public class StockAlertService {
    private static final Logger LOGGER = Logger.getLogger(StockAlertService.class.getName());
    private static final int DEFAULT_THRESHOLD = 5;

    private final ProductService productService;
    private final ScheduledExecutorService scheduler;
    private boolean isSchedulerRunning = false;

    public StockAlertService() {
        this.productService = new ProductService();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Check for low-stock products
     * 
     * @return List of products where quantity <= threshold
     */
    public List<Product> checkLowStock() {
        List<Product> lowStockProducts = new ArrayList<>();

        try {
            LOGGER.info("🔍 Starting low-stock check...");

            List<Product> allProducts = productService.getAllProducts();

            if (allProducts == null || allProducts.isEmpty()) {
                LOGGER.info("📭 No products found in inventory");
                return lowStockProducts;
            }

            // Filter products where quantity <= threshold
            for (Product product : allProducts) {
                if (product.isLowStock()) {
                    lowStockProducts.add(product);
                    LOGGER.warning(String.format("⚠️ LOW STOCK: %s | Qty: %d | Threshold: %d",
                            product.getName(), product.getQuantity(), product.getThresholdLimit()));
                }
            }

            if (lowStockProducts.isEmpty()) {
                LOGGER.info("✅ All products are sufficiently stocked");
            } else {
                LOGGER.warning(String.format("⚠️ Found %d products with low stock", lowStockProducts.size()));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error checking low stock: " + e.getMessage(), e);
        }

        return lowStockProducts;
    }

    /**
     * Send low-stock alert email to admin
     * 
     * @param lowStockProducts List of products with low stock
     * @param adminEmail       Admin email address
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendLowStockAlert(List<Product> lowStockProducts, String adminEmail) {
        if (lowStockProducts == null || lowStockProducts.isEmpty()) {
            LOGGER.info("ℹ️ No low-stock products to report");
            return false;
        }

        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            LOGGER.severe("❌ Admin email address is required");
            return false;
        }

        try {
            LOGGER.info("📧 Preparing low-stock alert email...");

            String subject = "⚠️ Low Stock Alert - Inventory Management System";
            String body = buildAlertEmailBody(lowStockProducts);

            // Send email using EmailService
            EmailService.sendReport(adminEmail, subject, body, null);

            LOGGER.info("✅ Low-stock alert email sent successfully to: " + adminEmail);
            return true;

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "❌ Failed to send low-stock alert email: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Unexpected error sending alert: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Build formatted email body for low-stock alert
     * 
     * @param lowStockProducts List of products with low stock
     * @return Formatted email body
     */
    private String buildAlertEmailBody(List<Product> lowStockProducts) {
        StringBuilder body = new StringBuilder();

        body.append("⚠️ LOW STOCK ALERT ⚠️\n\n");
        body.append("Dear Admin,\n\n");
        body.append("The following products are running low on stock and require immediate attention:\n\n");
        body.append("═══════════════════════════════════════════════════════════\n\n");

        int count = 1;
        for (Product product : lowStockProducts) {
            body.append(String.format("%d. Product: %s\n", count, product.getName()));
            body.append(String.format("   • Product ID: %d\n", product.getId()));
            body.append(String.format("   • Category: %s\n", product.getCategory()));
            body.append(String.format("   • Current Quantity: %d units\n", product.getQuantity()));
            body.append(String.format("   • Threshold Limit: %d units\n", product.getThresholdLimit()));
            body.append(String.format("   • Status: ⚠️ %s\n",
                    product.getQuantity() == 0 ? "OUT OF STOCK" : "LOW STOCK"));

            if (product.getQuantity() < product.getThresholdLimit()) {
                body.append(String.format("   • Suggested Restock: %d units\n",
                        product.getStockDeficit()));
            }

            body.append("\n");
            count++;
        }

        body.append("═══════════════════════════════════════════════════════════\n\n");
        body.append(String.format("Total Products Requiring Attention: %d\n\n", lowStockProducts.size()));
        body.append("ACTION REQUIRED:\n");
        body.append("• Review the above products immediately\n");
        body.append("• Contact suppliers for restocking\n");
        body.append("• Update inventory once restocked\n\n");
        body.append("This is an automated alert from your Inventory Management System.\n");
        body.append("Generated on: " + new java.util.Date() + "\n\n");
        body.append("Best regards,\n");
        body.append("Inventory Management System\n");
        body.append("Automated Stock Monitoring Service");

        return body.toString();
    }

    /**
     * Start automated daily stock monitoring
     * Runs every 24 hours
     * 
     * @param adminEmail Email address to send alerts to
     */
    public void startAutomatedMonitoring(String adminEmail) {
        if (isSchedulerRunning) {
            LOGGER.warning("⚠️ Automated monitoring is already running");
            return;
        }

        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            LOGGER.severe("❌ Cannot start monitoring: Admin email is required");
            return;
        }

        LOGGER.info("🚀 Starting automated stock monitoring...");
        LOGGER.info("📧 Alert notifications will be sent to: " + adminEmail);
        LOGGER.info("⏰ Schedule: Every 24 hours");

        // Schedule the task to run immediately and then every 24 hours
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LOGGER.info("⏰ Running scheduled low-stock check...");

                List<Product> lowStockProducts = checkLowStock();

                if (!lowStockProducts.isEmpty()) {
                    LOGGER.warning(String.format("⚠️ Detected %d low-stock products", lowStockProducts.size()));

                    boolean emailSent = sendLowStockAlert(lowStockProducts, adminEmail);

                    if (emailSent) {
                        LOGGER.info("✅ Low-stock alert sent successfully");
                    } else {
                        LOGGER.severe("❌ Failed to send low-stock alert");
                    }
                } else {
                    LOGGER.info("✅ No low-stock items detected in scheduled check");
                }

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "❌ Error in scheduled stock check: " + e.getMessage(), e);
            }
        }, 0, 24, TimeUnit.HOURS); // Run immediately, then every 24 hours

        isSchedulerRunning = true;
        LOGGER.info("✅ Automated monitoring started successfully");
    }

    /**
     * Start automated monitoring with custom interval
     * 
     * @param adminEmail Email address to send alerts to
     * @param interval   Time interval between checks
     * @param timeUnit   Time unit (HOURS, MINUTES, DAYS, etc.)
     */
    public void startAutomatedMonitoring(String adminEmail, long interval, TimeUnit timeUnit) {
        if (isSchedulerRunning) {
            LOGGER.warning("⚠️ Automated monitoring is already running");
            return;
        }

        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            LOGGER.severe("❌ Cannot start monitoring: Admin email is required");
            return;
        }

        LOGGER.info("🚀 Starting automated stock monitoring...");
        LOGGER.info("📧 Alert notifications will be sent to: " + adminEmail);
        LOGGER.info(String.format("⏰ Schedule: Every %d %s", interval, timeUnit.toString().toLowerCase()));

        scheduler.scheduleAtFixedRate(() -> {
            try {
                LOGGER.info("⏰ Running scheduled low-stock check...");

                List<Product> lowStockProducts = checkLowStock();

                if (!lowStockProducts.isEmpty()) {
                    sendLowStockAlert(lowStockProducts, adminEmail);
                }

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "❌ Error in scheduled stock check: " + e.getMessage(), e);
            }
        }, 0, interval, timeUnit);

        isSchedulerRunning = true;
        LOGGER.info("✅ Automated monitoring started successfully");
    }

    /**
     * Stop automated monitoring
     */
    public void stopAutomatedMonitoring() {
        if (!isSchedulerRunning) {
            LOGGER.info("ℹ️ Automated monitoring is not running");
            return;
        }

        LOGGER.info("🛑 Stopping automated stock monitoring...");

        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            isSchedulerRunning = false;
            LOGGER.info("✅ Automated monitoring stopped successfully");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "❌ Error stopping monitoring: " + e.getMessage(), e);
        }
    }

    /**
     * Check if automated monitoring is running
     * 
     * @return true if scheduler is running, false otherwise
     */
    public boolean isMonitoringActive() {
        return isSchedulerRunning;
    }

    /**
     * Get status report of current stock levels
     * 
     * @return Formatted status report
     */
    public String getStockStatusReport() {
        StringBuilder report = new StringBuilder();

        try {
            List<Product> allProducts = productService.getAllProducts();
            List<Product> lowStockProducts = checkLowStock();

            int totalProducts = allProducts != null ? allProducts.size() : 0;
            int lowStockCount = lowStockProducts.size();
            int healthyStockCount = totalProducts - lowStockCount;

            report.append("\n╔═══════════════════════════════════════════════════════════╗\n");
            report.append("║           INVENTORY STOCK STATUS REPORT                  ║\n");
            report.append("╚═══════════════════════════════════════════════════════════╝\n\n");

            report.append(String.format("📊 Total Products: %d\n", totalProducts));
            report.append(String.format("✅ Healthy Stock: %d (%.1f%%)\n",
                    healthyStockCount,
                    totalProducts > 0 ? (healthyStockCount * 100.0 / totalProducts) : 0));
            report.append(String.format("⚠️ Low Stock: %d (%.1f%%)\n",
                    lowStockCount,
                    totalProducts > 0 ? (lowStockCount * 100.0 / totalProducts) : 0));
            report.append(String.format("🤖 Monitoring Status: %s\n\n",
                    isSchedulerRunning ? "🟢 ACTIVE" : "🔴 INACTIVE"));

            if (lowStockCount > 0) {
                report.append("⚠️ PRODUCTS REQUIRING ATTENTION:\n");
                report.append("─────────────────────────────────────────────────────────\n");

                for (Product product : lowStockProducts) {
                    report.append(String.format("• %s (ID: %d) | Qty: %d/%d\n",
                            product.getName(),
                            product.getId(),
                            product.getQuantity(),
                            product.getThresholdLimit()));
                }
            } else {
                report.append("✅ All products are sufficiently stocked!\n");
            }

        } catch (Exception e) {
            report.append("❌ Error generating report: ").append(e.getMessage()).append("\n");
        }

        return report.toString();
    }

    /**
     * Cleanup resources
     */
    public void shutdown() {
        if (isSchedulerRunning) {
            stopAutomatedMonitoring();
        }
    }
}
