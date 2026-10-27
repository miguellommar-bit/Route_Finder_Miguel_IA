package com.miguel.routefinder.repositories;

import com.miguel.routefinder.model.Airport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AirportRepository {
    public Airport getAirport(int airportID){
        String SQL = "SELECT * FROM airport WHERE airportID = ?";
        return null;
    }
    public Airport getAirport(double latitude, double longitude){
        return null;
    }
    public boolean insertAirport(Airport airport){
        return false;
    }
    public boolean updateAirport(int airportID, Airport airport){
        return false;
    }
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:routefinder.db");
    }
}
