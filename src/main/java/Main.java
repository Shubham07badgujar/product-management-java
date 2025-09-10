import java.util.Scanner;

import model.Product;

/**
 * Main class to demonstrate Product functionality
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Product Creation ===");

        // Get product ID from user
        System.out.print("Enter product ID: ");
        int id = scanner.nextInt();

        // Clear the buffer
        scanner.nextLine();

        // Get product name from user
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();

        // Get product price from user
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();

        // Create Product object using user input
        Product product = new Product(id, name, price);

        // Print the created product using toString()
        System.out.println("\nCreated product:");
        System.out.println(product);

        scanner.close();
    }
}
