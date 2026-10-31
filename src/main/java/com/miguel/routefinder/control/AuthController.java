package com.miguel.routefinder.control;

import com.miguel.routefinder.model.User;
import com.miguel.routefinder.repositories.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthController {

    private UserRepository userRepository = new UserRepository();

    private String message = "";

    public String getMessage() {
        return message;
    }

    public boolean login(String email, String password) {
        User foundUser = userRepository.getUserByEmail(email);

        if (foundUser == null) {
            message = "No account exists with that email.";
            return false;
        }

        String storedHash = foundUser.getHashPassword();
        boolean passwordCorrect = comparePassword(password, storedHash);

        if (passwordCorrect == false) {
            message = "Incorrect password.";
            return false;
        }

        message = "Login successful.";
        return true;
    }

    public boolean createAccount(String email, String password, String confirmPassword) {
        if (email.contains("@") == false) {
            message = "Please enter a valid email address.";
            return false;
        }

        if (password.equals(confirmPassword) == false) {
            message = "Passwords do not match.";
            return false;
        }

        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) {
            message = "An account with that email already exists.";
            return false;
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setRole("traveller");
        String hashedPassword = hashPassword(password);
        newUser.setHashPassword(hashedPassword);

        boolean inserted = userRepository.insertUser(newUser);

        if (inserted == false) {
            message = "Account could not be created. Please try again.";
            return false;
        }

        message = "Account created successfully.";
        return true;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digestTool = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digestTool.digest(password.getBytes());

            String hashText = "";
            for (int i = 0; i < hashBytes.length; i++) {
                byte currentByte = hashBytes[i];
                String hexPair = String.format("%02x", currentByte);
                hashText = hashText + hexPair;
            }
            return hashText;

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private boolean comparePassword(String input, String stored) {
        String inputHash = hashPassword(input);

        if (inputHash == null) {
            return false;
        }
        if (inputHash.equals(stored)) {
            return true;
        } else {
            return false;
        }
    }
}