package util;

import java.util.List;
import java.util.ArrayList;

/**
 * Pagination utility class for handling paginated data
 * Provides functionality to paginate lists of objects with page navigation
 */
public class PaginationUtil<T> {
    private final List<T> allData;
    private final int pageSize;
    private int currentPage;
    private final int totalPages;

    public PaginationUtil(List<T> data, int pageSize) {
        if (data == null) {
            this.allData = new ArrayList<>();
        } else {
            this.allData = new ArrayList<>(data);
        }

        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        this.pageSize = pageSize;
        this.currentPage = 1;
        this.totalPages = calculateTotalPages();
    }

    /**
     * Get data for the current page
     * 
     * @return List of items for current page
     */
    public List<T> getCurrentPageData() {
        return getPageData(currentPage);
    }

    /**
     * Get data for a specific page
     * 
     * @param page Page number (1-based)
     * @return List of items for specified page
     */
    public List<T> getPageData(int page) {
        if (page < 1 || page > totalPages) {
            return new ArrayList<>();
        }

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allData.size());

        return new ArrayList<>(allData.subList(startIndex, endIndex));
    }

    /**
     * Navigate to next page
     * 
     * @return true if navigation successful, false if already at last page
     */
    public boolean nextPage() {
        if (hasNextPage()) {
            currentPage++;
            return true;
        }
        return false;
    }

    /**
     * Navigate to previous page
     * 
     * @return true if navigation successful, false if already at first page
     */
    public boolean previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
            return true;
        }
        return false;
    }

    /**
     * Navigate to first page
     */
    public void firstPage() {
        currentPage = 1;
    }

    /**
     * Navigate to last page
     */
    public void lastPage() {
        currentPage = totalPages;
    }

    /**
     * Navigate to specific page
     * 
     * @param page Page number (1-based)
     * @return true if navigation successful, false if invalid page
     */
    public boolean goToPage(int page) {
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            return true;
        }
        return false;
    }

    /**
     * Check if there's a next page
     */
    public boolean hasNextPage() {
        return currentPage < totalPages;
    }

    /**
     * Check if there's a previous page
     */
    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    /**
     * Get current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get total number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Get page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Get total number of items
     */
    public int getTotalItems() {
        return allData.size();
    }

    /**
     * Get pagination info as formatted string
     */
    public String getPaginationInfo() {
        if (totalPages == 0) {
            return "No data available";
        }

        int startItem = ((currentPage - 1) * pageSize) + 1;
        int endItem = Math.min(currentPage * pageSize, getTotalItems());

        return String.format("Showing %d-%d of %d items (Page %d of %d)",
                startItem, endItem, getTotalItems(), currentPage, totalPages);
    }

    /**
     * Get page navigation display (e.g., "« Previous 1 2 [3] 4 5 Next »")
     */
    public String getPageNavigation() {
        if (totalPages <= 1) {
            return "";
        }

        StringBuilder nav = new StringBuilder();

        // Previous button
        if (hasPreviousPage()) {
            nav.append("« Previous ");
        } else {
            nav.append("« Previous ");
        }

        // Page numbers
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            nav.append("1 ");
            if (startPage > 2) {
                nav.append("... ");
            }
        }

        for (int i = startPage; i <= endPage; i++) {
            if (i == currentPage) {
                nav.append("[").append(i).append("] ");
            } else {
                nav.append(i).append(" ");
            }
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                nav.append("... ");
            }
            nav.append(totalPages).append(" ");
        }

        // Next button
        if (hasNextPage()) {
            nav.append("Next »");
        } else {
            nav.append("Next »");
        }

        return nav.toString().trim();
    }

    private int calculateTotalPages() {
        if (allData.isEmpty()) {
            return 0;
        }
        return (int) Math.ceil((double) allData.size() / pageSize);
    }

    /**
     * Create paginated result object
     */
    public PaginationResult<T> getPaginationResult() {
        return new PaginationResult<>(
                getCurrentPageData(),
                currentPage,
                totalPages,
                pageSize,
                getTotalItems(),
                hasNextPage(),
                hasPreviousPage());
    }
}

/**
 * Pagination result container class
 */
class PaginationResult<T> {
    private final List<T> data;
    private final int currentPage;
    private final int totalPages;
    private final int pageSize;
    private final int totalItems;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PaginationResult(List<T> data, int currentPage, int totalPages,
            int pageSize, int totalItems, boolean hasNext, boolean hasPrevious) {
        this.data = data;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    // Getters
    public List<T> getData() {
        return data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }
}