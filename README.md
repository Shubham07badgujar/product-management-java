# Product Management System

A simplified console-based Java application for managing product inventory with comprehensive exception handling and input validation.

## 🚀 Quick Start

### Running the Application
```bash
# Compile
javac -d target/classes src/main/java/**/*.java

# Run
java -cp target/classes main.Main
```

## 📋 Core Features

- ✅ **Add Product** - With duplicate ID prevention and input validation
- ✅ **View All Products** - Simple list display
- ✅ **Search Product by ID** - Find specific products
- ✅ **Update Product Quantity** - Modify stock levels
- ✅ **Remove Product** - Delete with confirmation
- ✅ **Exception Handling** - Robust input validation for all operations

## 🏗️ Simplified Structure
```
src/main/java/
├── model/Product.java       # Product entity (id, name, price, quantity)
├── service/ProductService.java  # Business logic (5 core methods)
└── main/Main.java          # Console interface (5 menu options)
```

## �️ Exception Handling Features

- **Input Validation**: All numeric inputs validated with range checks
- **Type Safety**: Try-catch blocks for NumberFormatException
- **Business Logic**: Duplicate ID prevention, negative value checks
- **User-Friendly**: Clear error messages and retry prompts
- **Bounds Checking**: Min/max validation for all inputs

## 💡 Sample Data
Pre-loaded products for testing:
- Laptop (ID: 1, $999.99, Qty: 5)
- Mouse (ID: 2, $25.50, Qty: 20)
- Keyboard (ID: 3, $75.00, Qty: 10)

## 🎯 Key Improvements
- **Concise Code**: Reduced from 400+ to ~180 lines
- **Better Exception Handling**: Comprehensive input validation
- **Simplified Menu**: 5 core operations instead of 9
- **Clean Architecture**: Focused on essential functionality
- **Modern Java**: Switch expressions and concise syntax