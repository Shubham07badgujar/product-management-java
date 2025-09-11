# Product Management System

A comprehensive console-based Java application for managing product inventory using Maven and Object-Oriented Programming principles.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+ (optional, for Maven commands)

### Running the Application
```bash
# Using Maven
mvn clean compile exec:java -Dexec.mainClass="main.Main"

# Using Java directly
javac -d target/classes src/main/java/**/*.java
java -cp target/classes main.Main
```

## 📋 Features

### Core Functionality
- ✅ Add new products with validation
- ✅ View all products in tabular format
- ✅ Search products by ID or name
- ✅ Update product quantity and price
- ✅ Remove products with confirmation
- ✅ Inventory statistics and analytics
- ✅ Low stock alerts and reporting

### Advanced Features
- 🎯 Sample data pre-loaded for testing
- 🛡️ Comprehensive input validation
- 📊 Rich analytics dashboard
- 🎨 User-friendly console interface
- ⚡ Real-time inventory calculations

## 🏗️ Project Structure
```
src/main/java/
├── model/           # Product domain objects
├── service/         # Business logic layer
└── main/           # User interface layer
```

## 📖 Documentation
See [PRODUCT_DOCUMENTATION.md](PRODUCT_DOCUMENTATION.md) for comprehensive technical documentation.

## 🎯 Learning Objectives
- Object-Oriented Programming concepts
- Maven project management
- Java collections and Stream API
- Error handling and validation
- User interface design
- Software architecture patterns

## 💡 Sample Usage
The application includes pre-loaded sample products for immediate testing:
- Gaming Laptop ($1299.99)
- Wireless Mouse ($29.99)  
- Mechanical Keyboard ($89.99)
- 4K Monitor ($399.99)
- USB-C Hub ($45.50) - Out of stock

## 🔧 Technical Stack
- **Java**: 17 (LTS)
- **Build Tool**: Maven 4.0.0
- **Architecture**: Layered (Model-Service-UI)
- **Data Storage**: In-memory ArrayList
- **Testing**: Manual console testing