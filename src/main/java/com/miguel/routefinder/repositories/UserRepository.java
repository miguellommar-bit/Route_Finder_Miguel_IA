package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.User;

import java.sql.*;
import java.time.LocalDate;

public class UserRepository {
    public boolean insertUser(User user){
        String SQL = "INSERT INTO \"User\" (Email, HashPassword, Role, Name, Surname, DateOfBirth) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

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

    public User getUserByEmail(String email){
        String SQL = "SELECT * FROM \"User\" WHERE Email = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

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

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:routefinder.db");
    }
}