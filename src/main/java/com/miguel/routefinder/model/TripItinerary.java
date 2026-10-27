package com.miguel.routefinder.model;

public class TripItinerary {
    private int tripID;
    private int flightID;
    private int legOrder;

    public int getTripID() {
        return tripID;
    }
    public void setTripID(int tripID) {
        this.tripID = tripID;
    }
    public int getFlightID() {
        return flightID;
    }
    public void setFlightID(int flightID) {
        this.flightID = flightID;
    }
    public int getLegOrder() {
        return legOrder;
    }
    public void setLegOrder(int legOrder) {
        this.legOrder = legOrder;
    }
}