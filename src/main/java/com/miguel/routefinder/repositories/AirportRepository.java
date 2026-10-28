package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.Airport;

import java.sql.*;

public class AirportRepository {
    public Airport getAirport(int airportID){
        String SQL = "SELECT * FROM airport WHERE airportID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setInt(1, airportID);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                Airport airport = new Airport();
                airport.setAirportID(results.getInt("AirportID"));
                airport.setName(results.getString("Name"));
                airport.setCity(results.getString("City"));
                airport.setLatitude(results.getDouble("Latitude"));
                airport.setLongitude(results.getDouble("Longitude"));
                airport.setCountry(results.getString("Country"));
                return airport;
            }
            return null;

        } catch (SQLException e) {
            return null;
        }
    }
    public Airport getAirport(double latitude, double longitude){
        String SQL = "SELECT * FROM airport WHERE Latitude = ? AND Longitude = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setDouble(1, latitude);
            statement.setDouble(2, longitude);

            ResultSet results = statement.executeQuery();

            if (results.next()) {
                Airport airport = new Airport();
                airport.setAirportID(results.getInt("AirportID"));
                airport.setName(results.getString("Name"));
                airport.setCity(results.getString("City"));
                airport.setLatitude(results.getDouble("Latitude"));
                airport.setLongitude(results.getDouble("Longitude"));
                airport.setCountry(results.getString("Country"));
                return airport;
            }
            return null;

        } catch (SQLException e) {
            return null;
        }
    }
    public boolean insertAirport(Airport airport){
        String SQL = "INSERT INTO Airport (Name, City, Country, Latitude, Longitude) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setString(1, airport.getName());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCountry());
            statement.setDouble(5, airport.getLongitude());
            statement.setDouble(4, airport.getLatitude());

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
        return false;
    }
    }
    public boolean updateAirport(int airportID, Airport airport){
        String SQL = "UPDATE Airport SET Name = ?, City = ?, Country = ?, Latitude = ?, Longitude = ? WHERE AirportID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setInt(6, airportID);
            statement.setString(1, airport.getName());
            statement.setString(2, airport.getCity());
            statement.setString(3, airport.getCountry());
            statement.setDouble(5, airport.getLongitude());
            statement.setDouble(4, airport.getLatitude());


            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            return false;
        }
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:routefinder.db");
    }
}
