package com.miguel.routefinder.model;

public class Airport {
    private int airportID;
    private String name;
    private String city;
    private String country;
    private double latitude;
    private double longitude;

    public int getAirportID() {
        return airportID;
    }
    public void setAirportID(int airportID) {
        this.airportID = airportID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country; }

    public void setCountry(String country) {
        this.country = country;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}