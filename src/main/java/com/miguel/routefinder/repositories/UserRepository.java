package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public boolean insertUser(User user) {
        String sql = "INSERT INTO \"User\" (Email, HashPassword, Role, Name, Surname, DateOfBirth) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getHashPassword());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getName());
            statement.setString(5, user.getSurname());
            if (user.getDateOfBirth() == null) {
                statement.setString(6, null);
            } else {
                statement.setString(6, user.getDateOfBirth().toString());
            }

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM \"User\" WHERE Email = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                User user = new User();
                user.setUserID(results.getInt("UserID"));
                user.setEmail(results.getString("Email"));
                user.setHashPassword(results.getString("HashPassword"));
                user.setRole(results.getString("Role"));
                user.setName(results.getString("Name"));
                user.setSurname(results.getString("Surname"));
                String dob = results.getString("DateOfBirth");
                if (dob != null) {
                    user.setDateOfBirth(LocalDate.parse(dob));
                }
                return user;
            }
            return null;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM \"User\" ORDER BY UserID";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet results = statement.executeQuery();

            while (results.next()) {
                User user = new User();
                user.setUserID(results.getInt("UserID"));
                user.setEmail(results.getString("Email"));
                user.setHashPassword(results.getString("HashPassword"));
                user.setRole(results.getString("Role"));
                user.setName(results.getString("Name"));
                user.setSurname(results.getString("Surname"));
                String dob = results.getString("DateOfBirth");
                if (dob != null) {
                    user.setDateOfBirth(LocalDate.parse(dob));
                }
                list.add(user);
            }
            return list;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return list;
        }
    }

    public boolean updateUserRole(int userID, String role) {
        String sql = "UPDATE \"User\" SET Role = ? WHERE UserID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role);
            statement.setInt(2, userID);

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:routefinder.db");
    }
}