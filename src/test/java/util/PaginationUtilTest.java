package util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Product;

/**
 * Tests for PaginationUtil utility class
 */
class PaginationUtilTest {

    private List<Product> testProducts;
    private PaginationUtil<Product> paginationUtil;

    @BeforeEach
    void setUp() {
        // Create test products
        testProducts = Arrays.asList(
                new Product(1, "Product 1", 10.0, 1, "Category1"),
                new Product(2, "Product 2", 20.0, 2, "Category2"),
                new Product(3, "Product 3", 30.0, 3, "Category3"),
                new Product(4, "Product 4", 40.0, 4, "Category4"),
                new Product(5, "Product 5", 50.0, 5, "Category5"),
                new Product(6, "Product 6", 60.0, 6, "Category6"),
                new Product(7, "Product 7", 70.0, 7, "Category7"),
                new Product(8, "Product 8", 80.0, 8, "Category8"),
                new Product(9, "Product 9", 90.0, 9, "Category9"),
                new Product(10, "Product 10", 100.0, 10, "Category10"));
    }

    @Test
    void testConstructor_ValidParameters_ShouldCreatePagination() {
        // Act
        paginationUtil = new PaginationUtil<>(testProducts, 3);

        // Assert
        assertNotNull(paginationUtil);
        assertEquals(10, paginationUtil.getTotalItems());
        assertEquals(3, paginationUtil.getPageSize());
        assertEquals(4, paginationUtil.getTotalPages()); // 10 items / 3 per page = 4 pages (ceiling)
        assertEquals(1, paginationUtil.getCurrentPage());
    }

    @Test
    void testConstructor_EmptyList_ShouldCreateEmptyPagination() {
        // Act
        paginationUtil = new PaginationUtil<>(Collections.emptyList(), 5);

        // Assert
        assertNotNull(paginationUtil);
        assertEquals(0, paginationUtil.getTotalItems());
        assertEquals(5, paginationUtil.getPageSize());
        assertEquals(0, paginationUtil.getTotalPages());
        assertEquals(1, paginationUtil.getCurrentPage());
    }

    @Test
    void testConstructor_NullList_ShouldHandleGracefully() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            paginationUtil = new PaginationUtil<>(null, 5);
            // Depending on implementation, this might handle null gracefully
            // or the implementation should be updated to handle null
        });
    }

    @Test
    void testConstructor_ZeroPageSize_ShouldHandleGracefully() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            paginationUtil = new PaginationUtil<>(testProducts, 0);
            // Implementation should handle zero or negative page sizes
        });
    }

    @Test
    void testGetCurrentPageData_FirstPage_ShouldReturnCorrectItems() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);

        // Act
        List<Product> currentPageData = paginationUtil.getCurrentPageData();

        // Assert
        assertNotNull(currentPageData);
        assertEquals(3, currentPageData.size());
        assertEquals("Product 1", currentPageData.get(0).getName());
        assertEquals("Product 2", currentPageData.get(1).getName());
        assertEquals("Product 3", currentPageData.get(2).getName());
    }

    @Test
    void testGetCurrentPageData_LastPage_ShouldReturnRemainingItems() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        paginationUtil.lastPage();

        // Act
        List<Product> currentPageData = paginationUtil.getCurrentPageData();

        // Assert
        assertNotNull(currentPageData);
        assertEquals(1, currentPageData.size()); // Last page has only 1 item (10 % 3 = 1)
        assertEquals("Product 10", currentPageData.get(0).getName());
    }

    @Test
    void testNextPage_FromFirstPage_ShouldMoveToSecondPage() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        assertEquals(1, paginationUtil.getCurrentPage());

        // Act
        boolean result = paginationUtil.nextPage();

        // Assert
        assertTrue(result);
        assertEquals(2, paginationUtil.getCurrentPage());

        List<Product> currentPageData = paginationUtil.getCurrentPageData();
        assertEquals(3, currentPageData.size());
        assertEquals("Product 4", currentPageData.get(0).getName());
    }

    @Test
    void testNextPage_FromLastPage_ShouldReturnFalse() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        paginationUtil.lastPage();
        int lastPage = paginationUtil.getCurrentPage();

        // Act
        boolean result = paginationUtil.nextPage();

        // Assert
        assertFalse(result);
        assertEquals(lastPage, paginationUtil.getCurrentPage()); // Should remain on last page
    }

    @Test
    void testPreviousPage_FromSecondPage_ShouldMoveToFirstPage() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        paginationUtil.nextPage(); // Move to page 2
        assertEquals(2, paginationUtil.getCurrentPage());

        // Act
        boolean result = paginationUtil.previousPage();

        // Assert
        assertTrue(result);
        assertEquals(1, paginationUtil.getCurrentPage());
    }

    @Test
    void testPreviousPage_FromFirstPage_ShouldReturnFalse() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        assertEquals(1, paginationUtil.getCurrentPage());

        // Act
        boolean result = paginationUtil.previousPage();

        // Assert
        assertFalse(result);
        assertEquals(1, paginationUtil.getCurrentPage()); // Should remain on first page
    }

    @Test
    void testFirstPage_ShouldMoveToFirstPage() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        paginationUtil.lastPage(); // Move to last page first

        // Act
        paginationUtil.firstPage();

        // Assert
        assertEquals(1, paginationUtil.getCurrentPage());

        List<Product> currentPageData = paginationUtil.getCurrentPageData();
        assertEquals("Product 1", currentPageData.get(0).getName());
    }

    @Test
    void testLastPage_ShouldMoveToLastPage() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        assertEquals(1, paginationUtil.getCurrentPage());

        // Act
        paginationUtil.lastPage();

        // Assert
        assertEquals(4, paginationUtil.getCurrentPage()); // 4th page is the last page

        List<Product> currentPageData = paginationUtil.getCurrentPageData();
        assertEquals(1, currentPageData.size());
        assertEquals("Product 10", currentPageData.get(0).getName());
    }

    @Test
    void testGoToPage_ValidPage_ShouldMoveToSpecifiedPage() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);

        // Act
        boolean result = paginationUtil.goToPage(3);

        // Assert
        assertTrue(result);
        assertEquals(3, paginationUtil.getCurrentPage());

        List<Product> currentPageData = paginationUtil.getCurrentPageData();
        assertEquals(3, currentPageData.size());
        assertEquals("Product 7", currentPageData.get(0).getName());
    }

    @Test
    void testGoToPage_InvalidPage_ShouldReturnFalse() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);
        int originalPage = paginationUtil.getCurrentPage();

        // Act & Assert
        assertFalse(paginationUtil.goToPage(0)); // Page 0 is invalid
        assertFalse(paginationUtil.goToPage(-1)); // Negative page is invalid
        assertFalse(paginationUtil.goToPage(5)); // Page 5 doesn't exist (only 4 pages)

        // Should remain on original page
        assertEquals(originalPage, paginationUtil.getCurrentPage());
    }

    @Test
    void testGetTotalPages_VariousPageSizes_ShouldCalculateCorrectly() {
        // Test different page sizes

        // 10 items, page size 1 = 10 pages
        paginationUtil = new PaginationUtil<>(testProducts, 1);
        assertEquals(10, paginationUtil.getTotalPages());

        // 10 items, page size 5 = 2 pages
        paginationUtil = new PaginationUtil<>(testProducts, 5);
        assertEquals(2, paginationUtil.getTotalPages());

        // 10 items, page size 10 = 1 page
        paginationUtil = new PaginationUtil<>(testProducts, 10);
        assertEquals(1, paginationUtil.getTotalPages());

        // 10 items, page size 15 = 1 page (all items fit in one page)
        paginationUtil = new PaginationUtil<>(testProducts, 15);
        assertEquals(1, paginationUtil.getTotalPages());
    }

    @Test
    void testPaginationFlow_CompleteNavigation_ShouldWorkCorrectly() {
        // Arrange
        paginationUtil = new PaginationUtil<>(testProducts, 3);

        // Test complete navigation flow
        assertEquals(1, paginationUtil.getCurrentPage());

        // Move through all pages
        assertTrue(paginationUtil.nextPage()); // Page 2
        assertTrue(paginationUtil.nextPage()); // Page 3
        assertTrue(paginationUtil.nextPage()); // Page 4 (last)
        assertFalse(paginationUtil.nextPage()); // Can't go beyond last page

        assertEquals(4, paginationUtil.getCurrentPage());

        // Move back through pages
        assertTrue(paginationUtil.previousPage()); // Page 3
        assertTrue(paginationUtil.previousPage()); // Page 2
        assertTrue(paginationUtil.previousPage()); // Page 1
        assertFalse(paginationUtil.previousPage()); // Can't go before first page

        assertEquals(1, paginationUtil.getCurrentPage());
    }

    @Test
    void testPaginationWithSingleItem_ShouldWorkCorrectly() {
        // Arrange
        List<Product> singleItem = Collections.singletonList(
                new Product(1, "Single Product", 10.0, 1, "Category"));
        paginationUtil = new PaginationUtil<>(singleItem, 5);

        // Assert
        assertEquals(1, paginationUtil.getTotalItems());
        assertEquals(1, paginationUtil.getTotalPages());
        assertEquals(1, paginationUtil.getCurrentPage());

        List<Product> currentPageData = paginationUtil.getCurrentPageData();
        assertEquals(1, currentPageData.size());
        assertEquals("Single Product", currentPageData.get(0).getName());

        // Navigation should not work
        assertFalse(paginationUtil.nextPage());
        assertFalse(paginationUtil.previousPage());
    }

    @Test
    void testPaginationEdgeCases_ShouldHandleGracefully() {
        // Test with exact page boundaries
        List<Product> exactBoundary = Arrays.asList(
                new Product(1, "Product 1", 10.0, 1, "Category1"),
                new Product(2, "Product 2", 20.0, 2, "Category2"),
                new Product(3, "Product 3", 30.0, 3, "Category3"));

        // 3 items with page size 3 should result in exactly 1 page
        paginationUtil = new PaginationUtil<>(exactBoundary, 3);
        assertEquals(1, paginationUtil.getTotalPages());
        assertEquals(3, paginationUtil.getCurrentPageData().size());

        // Should not be able to navigate
        assertFalse(paginationUtil.nextPage());
        assertFalse(paginationUtil.previousPage());
    }
}