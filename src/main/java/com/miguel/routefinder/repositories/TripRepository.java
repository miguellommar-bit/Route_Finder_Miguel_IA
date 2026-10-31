package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRepository {
    // Returns the new TripID, or -1 if the insert failed
    public int insertTrip(int userID){
        String SQL = "INSERT INTO Trip (UserID) VALUES (?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userID);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            return -1;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public boolean insertTripItinerary(int tripID, int flightID, int legOrder){
        String SQL = "INSERT INTO TripItinerary (TripID, FlightID, LegOrder) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setInt(1, tripID);
            statement.setInt(2, flightID);
            statement.setInt(3, legOrder);

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Trip> getTripsByUser(int userID){
        List<Trip> trips = new ArrayList<>();
        String SQL = "SELECT Trip.TripID, Trip.UserID FROM Trip " +
                "LEFT JOIN TripItinerary ON Trip.TripID = TripItinerary.TripID " +
                "LEFT JOIN Flight ON TripItinerary.FlightID = Flight.FlightID " +
                "WHERE Trip.UserID = ? GROUP BY Trip.TripID ORDER BY MIN(Flight.DepartureDateTime)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setInt(1, userID);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Trip trip = new Trip();
                trip.setTripID(results.getInt("TripID"));
                trip.setUserID(results.getInt("UserID"));
                trips.add(trip);
            }
            return trips;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return trips;
        }
    }
    public List<Integer> getFlightIDsByTrip(int tripID){
        List<Integer> flightIDs = new ArrayList<>();
        String SQL = "SELECT FlightID FROM TripItinerary WHERE TripID = ? ORDER BY LegOrder";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

            statement.setInt(1, tripID);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                flightIDs.add(results.getInt("FlightID"));
            }
            return flightIDs;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return flightIDs;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:routefinder.db");
    }
}