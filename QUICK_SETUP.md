# 🚀 Quick Setup Guide - Authentication System

## 📋 Prerequisites
- ✅ Java 11 or higher installed
- ✅ MySQL 8.0+ running
- ✅ Database created (e.g., `product_management_db`)
- ✅ MySQL JDBC driver in `lib/` folder

---

## ⚡ 5-Minute Setup

### Step 1: Configure Database (2 minutes)

1. **Open `src/main/resources/database.properties`**
   
   Update with your MySQL credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/your_database_name
   db.username=your_mysql_username
   db.password=your_mysql_password
   ```

2. **Run the SQL setup script**
   
   ```bash
   # Option A: Command line
   mysql -u root -p your_database_name < setup_users_with_auth.sql
   
   # Option B: MySQL Workbench
   # Open setup_users_with_auth.sql and execute
   ```

   This creates:
   - ✅ `users` table with authentication fields
   - ✅ Test admin account: `admin@test.com` / `admin123`
   - ✅ Test user account: `user@test.com` / `user123`

---

### Step 2: Run the Application (1 minute)

**Windows:**
```bash
# Double-click this file or run in terminal:
run_with_auth.bat
```

**Manual Run:**
```bash
# Compile
javac -d target/classes -cp "lib/*" src/main/java/main/MainWithAuth.java

# Run
java -cp "target/classes;lib/*" main.MainWithAuth
```

---

### Step 3: Test the System (2 minutes)

#### Test 1: Login with Test Admin Account
```
Choose option: 2 (Login)
Email: admin@test.com
Password: admin123
```
✅ **Expected:** Admin menu with 8 options (full access)

#### Test 2: Login with Test User Account
```
Logout and login again
Email: user@test.com
Password: user123
```
✅ **Expected:** User menu with 4 options (view-only)

#### Test 3: Create New Account
```
Choose option: 1 (Register)
First Name: Your Name
Last Name: Your Last Name
Email: your.email@example.com
Phone: 1234567890
Password: secure123
Confirm: secure123
Role: 1 (User) or 2 (Admin)
```
✅ **Expected:** Registration successful message

---

## 🎯 What You Can Do

### As a User (View-Only):
- 👀 View all products with pagination
- 🔍 Search products by name, category, or ID
- 💰 Filter products by price range
- 🚪 Logout

### As an Admin (Full Access):
- ➕ Add new products
- 👀 View all products
- 🔍 Search products
- ✏️ Update existing products
- 🗑️ Delete products
- 💰 Filter by price range
- 💾 Export to CSV
- 🚪 Logout

---

## 🧪 Running Tests (Optional)

```bash
# Run all authentication tests
mvn test -Dtest=AuthServiceTest

# Expected: 15 tests passing
```

---

## 📖 Need More Help?

- **Full Documentation:** `AUTHENTICATION_GUIDE.md`
- **Implementation Details:** `AUTHENTICATION_IMPLEMENTATION_SUMMARY.md`
- **User Guide:** `USER_GUIDE.md`

---

## 🐛 Troubleshooting

### Problem: Database connection failed
**Solution:** 
1. Check MySQL is running
2. Verify `database.properties` has correct credentials
3. Ensure database exists

### Problem: Login fails with test accounts
**Solution:**
1. Run `setup_users_with_auth.sql` again
2. Verify users table exists: `SELECT * FROM users;`

### Problem: Compilation errors
**Solution:**
1. Check Java version: `java -version` (need 11+)
2. Ensure all dependencies in `lib/` folder
3. Run: `run_with_auth.bat`

---

## 🎉 You're All Set!

Your authentication system is ready. Start by logging in with:
- **Admin:** `admin@test.com` / `admin123`
- **User:** `user@test.com` / `user123`

Enjoy managing your products! 🚀
