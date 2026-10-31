package com.miguel.routefinder.view;

import com.miguel.routefinder.model.Airport;
import com.miguel.routefinder.model.Flight;

import java.time.LocalDateTime;
import java.util.Scanner;

public class AdminView extends TravellerView {

    public void displayAdminMenu() {
        System.out.println();
        System.out.println("1. Find route");
        System.out.println("2. Edit flight");
        System.out.println("3. Edit airport");
        System.out.println("4. View flight history");
        System.out.println("5. Exit");
    }

    public Flight[] getFlightEditInput() {
        Scanner scanner = new Scanner(System.in);
        Flight[] flights = new Flight[2];
        try {
            System.out.print("ID of the flight to edit: ");
            int flightID = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New origin airport ID: ");
            int originID = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New destination airport ID: ");
            int destinationID = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New departure (format 2026-11-01T09:00): ");
            LocalDateTime departure = LocalDateTime.parse(scanner.nextLine().trim());
            System.out.print("New arrival (format 2026-11-01T11:25): ");
            LocalDateTime arrival = LocalDateTime.parse(scanner.nextLine().trim());

            Flight oldFlight = new Flight();
            oldFlight.setFlightID(flightID);

            Flight newFlight = new Flight();
            newFlight.setOriginAirportID(originID);
            newFlight.setDestinationAirportID(destinationID);
            newFlight.setDepartureDateTime(departure);
            newFlight.setArrivalDateTime(arrival);

            flights[0] = oldFlight;
            flights[1] = newFlight;
            return flights;

        } catch (Exception e) {
            return null;
        }
    }

    public Airport[] getAirportEditInput() {
        Scanner scanner = new Scanner(System.in);
        Airport[] airports = new Airport[2];
        try {
            System.out.print("ID of the airport to edit: ");
            int airportID = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New name: ");
            String name = scanner.nextLine();
            System.out.print("New city: ");
            String city = scanner.nextLine();
            System.out.print("New country: ");
            String country = scanner.nextLine();
            System.out.print("New latitude: ");
            double latitude = Double.parseDouble(scanner.nextLine().trim());
            System.out.print("New longitude: ");
            double longitude = Double.parseDouble(scanner.nextLine().trim());

            Airport oldAirport = new Airport();
            oldAirport.setAirportID(airportID);

            Airport newAirport = new Airport();
            newAirport.setName(name);
            newAirport.setCity(city);
            newAirport.setCountry(country);
            newAirport.setLatitude(latitude);
            newAirport.setLongitude(longitude);

            airports[0] = oldAirport;
            airports[1] = newAirport;
            return airports;

        } catch (Exception e) {
            return null;
        }
    }
}