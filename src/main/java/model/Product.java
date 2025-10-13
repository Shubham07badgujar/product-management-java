package model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String category;

    public Product(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = "General"; // Default category
    }

    public Product(int id, String name, double price, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category != null && !category.trim().isEmpty() ? category : "General";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null && !category.trim().isEmpty() ? category : "General";
    }

    public double getTotalValue() {
        return price * quantity;
    }

    public boolean isInStock() {
        return quantity > 0;
    }

    @Override
    public String toString() {
        return String.format(
                "📦 Product [🆔 ID=%d, 📝 Name='%s', 💰 Price=$%.2f, 📊 Qty=%d, 🏷️ Category='%s', 💵 Total=$%.2f, %s]",
                id, name, price, quantity, category, getTotalValue(),
                isInStock() ? "✅ In Stock" : "❌ Out of Stock");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Product product = (Product) obj;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
