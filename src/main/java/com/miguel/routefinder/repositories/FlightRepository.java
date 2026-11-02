package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.Airport;
import com.miguel.routefinder.model.Flight;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightRepository {

    public Flight findFlight(Airport origin, Airport destination) {
        String sql = "SELECT * FROM Flight WHERE OriginAirportID = ? AND DestinationAirportID = ? " +
                "ORDER BY julianday(ArrivalDateTime) - julianday(DepartureDateTime) LIMIT 1";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, origin.getAirportID());
            statement.setInt(2, destination.getAirportID());
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                Flight flight = new Flight();
                flight.setFlightID(results.getInt("FlightID"));
                flight.setOriginAirportID(results.getInt("OriginAirportID"));
                flight.setDestinationAirportID(results.getInt("DestinationAirportID"));
                flight.setArrivalDateTime(LocalDateTime.parse(results.getString("ArrivalDateTime")));
                flight.setDepartureDateTime(LocalDateTime.parse(results.getString("DepartureDateTime")));
                return flight;
            }
            return null;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean insertFlight(Flight flight) {
        String sql = "INSERT INTO Flight (OriginAirportID, DestinationAirportID, ArrivalDateTime, DepartureDateTime) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, flight.getOriginAirportID());
            statement.setInt(2, flight.getDestinationAirportID());
            statement.setString(3, flight.getArrivalDateTime().toString());
            statement.setString(4, flight.getDepartureDateTime().toString());

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean updateFlight(int flightID, Flight flight) {
        String sql = "UPDATE Flight SET OriginAirportID = ?, DestinationAirportID = ?, ArrivalDateTime = ?, DepartureDateTime = ? WHERE FlightID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, flight.getOriginAirportID());
            statement.setInt(2, flight.getDestinationAirportID());
            statement.setString(3, flight.getArrivalDateTime().toString());
            statement.setString(4, flight.getDepartureDateTime().toString());
            statement.setInt(5, flightID);

            int rows = statement.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Flight getFlight(int flightID) {
        String sql = "SELECT * FROM Flight WHERE FlightID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, flightID);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                Flight flight = new Flight();
                flight.setFlightID(results.getInt("FlightID"));
                flight.setOriginAirportID(results.getInt("OriginAirportID"));
                flight.setDestinationAirportID(results.getInt("DestinationAirportID"));
                flight.setArrivalDateTime(LocalDateTime.parse(results.getString("ArrivalDateTime")));
                flight.setDepartureDateTime(LocalDateTime.parse(results.getString("DepartureDateTime")));
                return flight;
            }
            return null;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Flight> getAllFlights() {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT * FROM Flight ORDER BY FlightID";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Flight flight = new Flight();
                flight.setFlightID(results.getInt("FlightID"));
                flight.setOriginAirportID(results.getInt("OriginAirportID"));
                flight.setDestinationAirportID(results.getInt("DestinationAirportID"));
                flight.setArrivalDateTime(LocalDateTime.parse(results.getString("ArrivalDateTime")));
                flight.setDepartureDateTime(LocalDateTime.parse(results.getString("DepartureDateTime")));
                list.add(flight);
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