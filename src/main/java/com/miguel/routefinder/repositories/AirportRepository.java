package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.Airport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirportRepository {

    public Airport getAirport(int airportID) {
        String sql = "SELECT * FROM Airport WHERE AirportID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, airportID);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                Airport airport = new Airport();
                airport.setAirportID(results.getInt("AirportID"));
                airport.setName(results.getString("Name"));
                airport.setCity(results.getString("City"));
                airport.setCountry(results.getString("Country"));
                airport.setLatitude(results.getDouble("Latitude"));
                airport.setLongitude(results.getDouble("Longitude"));
                return airport;
            }
            return null;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Airport getAirport(double latitude, double longitude) {
        String sql = "SELECT * FROM Airport WHERE Latitude = ? AND Longitude = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, latitude);
            statement.setDouble(2, longitude);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                Airport airport = new Airport();
                airport.setAirportID(results.getInt("AirportID"));
                airport.setName(results.getString("Name"));
                airport.setCity(results.getString("City"));
                airport.setCountry(results.getString("Country"));
                airport.setLatitude(results.getDouble("Latitude"));
                airport.setLongitude(results.getDouble("Longitude"));
                return airport;
            }
            return null;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean insertAirport(Airport airport) {
        String sql = "INSERT INTO Airport (Name, City, Country, Latitude, Longitude) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, airport.getName());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCountry());
            statement.setDouble(4, airport.getLatitude());
            statement.setDouble(5, airport.getLongitude());

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean updateAirport(int airportID, Airport airport) {
        String sql = "UPDATE Airport SET Name = ?, City = ?, Country = ?, Latitude = ?, Longitude = ? WHERE AirportID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, airport.getName());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCountry());
            statement.setDouble(4, airport.getLatitude());
            statement.setDouble(5, airport.getLongitude());
            statement.setInt(6, airportID);

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Airport> getAllAirports() {
        List<Airport> list = new ArrayList<>();
        String sql = "SELECT * FROM Airport ORDER BY AirportID";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Airport airport = new Airport();
                airport.setAirportID(results.getInt("AirportID"));
                airport.setName(results.getString("Name"));
                airport.setCity(results.getString("City"));
                airport.setCountry(results.getString("Country"));
                airport.setLatitude(results.getDouble("Latitude"));
                airport.setLongitude(results.getDouble("Longitude"));
                list.add(airport);
            }
            return list;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return list;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:routefinder.db");
    }
}