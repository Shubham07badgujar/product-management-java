package service;

import java.util.List;

import dao.UserDao;
import dao.UserDaoImpl;
import exception.DatabaseOperationException;
import exception.UserNotFoundException;
import exception.UserValidationException;
import model.User;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public boolean addUser(User user) {
        if (user == null) {
            System.err.println("Cannot add null user");
            return false;
        }

        try {
            if (userDao.existsById(user.getId())) {
                System.err.println("User with ID " + user.getId() + " already exists");
                return false;
            }

            return userDao.create(user);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while adding user: " + e.getMessage());
            return false;
        } catch (UserValidationException e) {
            System.err.println("User validation error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeUserById(int id) {
        try {
            if (!userDao.existsById(id)) {
                System.err.println("User with ID " + id + " does not exist");
                return false;
            }

            return userDao.deleteById(id);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while removing user: " + e.getMessage());
            return false;
        } catch (UserNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return false;
        }
    }

    public User getUserById(int id) {
        try {
            return userDao.findById(id);
        } catch (UserNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return null;
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while retrieving user: " + e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            return userDao.findAll();
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while retrieving all users: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    public boolean updateUser(User user) {
        if (user == null) {
            System.err.println("Cannot update null user");
            return false;
        }

        try {
            return userDao.update(user);
        } catch (UserNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return false;
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while updating user: " + e.getMessage());
            return false;
        } catch (UserValidationException e) {
            System.err.println("User validation error: " + e.getMessage());
            return false;
        }
    }

    public List<User> findUsersByUsername(String username) {
        try {
            return userDao.findByUsername(username);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while searching users by username: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    public List<User> findUsersByEmail(String email) {
        try {
            return userDao.findByEmail(email);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while searching users by email: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    public User findUserByUsernameAndEmail(String username, String email) {
        try {
            return userDao.findByUsernameAndEmail(username, email);
        } catch (UserNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return null;
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while finding user by username and email: " + e.getMessage());
            return null;
        }
    }

    public int getTotalUserCount() {
        try {
            return userDao.getTotalCount();
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while counting users: " + e.getMessage());
            return 0;
        }
    }

    public boolean userExists(int id) {
        try {
            return userDao.existsById(id);
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while checking user existence: " + e.getMessage());
            return false;
        }
    }

    public void displayAllUsers() {
        List<User> users = getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n=== All Users ===");
        for (User user : users) {
            System.out.println(user);
        }
        System.out.println("Total users: " + users.size());
    }

    public void displayUserById(int id) {
        User user = getUserById(id);
        if (user != null) {
            System.out.println("User found:");
            System.out.println(user);
        } else {
            System.out.println("User with ID " + id + " not found.");
        }
    }

    // Password-related methods
    public User authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("Username and password cannot be empty");
            return null;
        }

        try {
            return userDao.authenticate(username, password);
        } catch (UserNotFoundException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return null;
        } catch (DatabaseOperationException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            return null;
        }
    }

    public boolean changePassword(int userId, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) {
            System.err.println("Password must be at least 6 characters long");
            return false;
        }

        try {
            return userDao.updatePassword(userId, newPassword);
        } catch (UserNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return false;
        } catch (DatabaseOperationException e) {
            System.err.println("Database error while changing password: " + e.getMessage());
            return false;
        } catch (UserValidationException e) {
            System.err.println("Password validation error: " + e.getMessage());
            return false;
        }
    }

    public boolean validateLogin(String username, String password) {
        User user = authenticateUser(username, password);
        return user != null;
    }
}