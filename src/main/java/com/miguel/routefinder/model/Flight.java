package com.miguel.routefinder.model;

import java.time.LocalDateTime;

public class Flight {

    private int flightID;
    private int originAirportID;

    private int destinationAirportID;
    private LocalDateTime arrivalDateTime;
    private LocalDateTime departureDateTime;

    public int getFlightID() {
        return flightID;
    }
    public void setFlightID(int flightID) {
        this.flightID = flightID; }
    public int getOriginAirportID() {
        return originAirportID; }
    public void setOriginAirportID(int originAirportID) {
        this.originAirportID = originAirportID; }
    public int getDestinationAirportID() {
        return destinationAirportID; }
    public void setDestinationAirportID(int destinationAirportID) {
        this.destinationAirportID = destinationAirportID; }
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime; }
    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime; }
    public LocalDateTime getDepartureDateTime() {
        return departureDateTime; }
    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime; }
}