package com.miguel.routefinder.control;
import com.miguel.routefinder.repositories.UserRepository;
import com.miguel.routefinder.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthController {

    private UserRepository userdb = new UserRepository();

    private String message = "";

    public String getMessage() {
        return message;
    }

    public boolean login(String email, String password) {
        User user = userdb.getUserByEmail(email);

        if (user == null) {
            message = "No account exists with that email.";
            return false;
        }

        String hash = user.getHashPassword();
        boolean correct = comparePassword(password, hash);

        if (correct == false) {
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

        User old = userdb.getUserByEmail(email);
        if (old != null) {
            message = "An account with that email already exists.";
            return false;
        }

        User user = new User();
        user.setEmail(email);
        user.setRole("traveller");
        String hash = hashPassword(password);
        user.setHashPassword(hash);

        boolean ok = userdb.insertUser(user);

        if (ok == false) {
            message = "Account could not be created. Please try again.";
            return false;
        }

        message = "Account created successfully.";
        return true;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest tool = MessageDigest.getInstance("SHA-256");
            byte[] bytes = tool.digest(password.getBytes());

            String text = "";
            for (int i = 0; i < bytes.length; i++) {
                byte one = bytes[i];
                String pair = String.format("%02x", one);
                text = text + pair;
            }
            return text;

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private boolean comparePassword(String input, String stored) {
        String hash = hashPassword(input);

        if (hash == null) {
            return false;
        }
        if (hash.equals(stored)) {
            return true;
        } else {
            return false;
        }
    }
}