package com.miguel.routefinder.control;

import com.miguel.routefinder.model.Airport;
import com.miguel.routefinder.model.Flight;
import com.miguel.routefinder.model.Route;
import com.miguel.routefinder.repositories.AirportRepository;
import com.miguel.routefinder.repositories.FlightRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RouteFinderController {

    private FlightRepository flightRepository = new FlightRepository();
    private AirportRepository airportRepository = new AirportRepository();

    public Route findRoute(Airport origin, Airport destination) {
        if (origin == null || destination == null) {
            return null;
        }
        if (origin.getAirportID() == destination.getAirportID()) {
            return null;
        }

        Flight directFlight = findDirectRoute(origin, destination);
        if (directFlight != null) {
            List<Flight> directList = new ArrayList<>();
            directList.add(directFlight);
            return buildRoute(directList);
        }

        List<Flight> connectingFlights = findConnectingRoute(origin, destination);
        if (connectingFlights == null) {
            return null;
        }
        return buildRoute(connectingFlights);
    }

    private Flight findDirectRoute(Airport origin, Airport destination) {
        Flight flight = flightRepository.findFlight(origin, destination);
        return flight;
    }

    private List<Flight> findConnectingRoute(Airport origin, Airport destination) {
        List<Airport> allAirports = new ArrayList<>();
        int nextID = 1;
        Airport loadedAirport = airportRepository.getAirport(nextID);
        while (loadedAirport != null) {
            allAirports.add(loadedAirport);
            nextID = nextID + 1;
            loadedAirport = airportRepository.getAirport(nextID);
        }

        List<Airport> openAirports = new ArrayList<>();
        List<Flight> openFlights = new ArrayList<>();
        List<Integer> openParents = new ArrayList<>();
        List<Double> openCosts = new ArrayList<>();
        List<Double> openScores = new ArrayList<>();

        List<Airport> closedAirports = new ArrayList<>();
        List<Flight> closedFlights = new ArrayList<>();
        List<Integer> closedParents = new ArrayList<>();
        List<Double> closedCosts = new ArrayList<>();

        openAirports.add(origin);
        openFlights.add(null);
        openParents.add(-1);
        openCosts.add(0.0);
        openScores.add(calculateHeuristic(origin, destination));

        while (openAirports.size() > 0) {
            int bestIndex = 0;
            for (int i = 1; i < openScores.size(); i++) {
                if (openScores.get(i) < openScores.get(bestIndex)) {
                    bestIndex = i;
                }
            }

            Airport currentAirport = openAirports.remove(bestIndex);
            Flight currentFlight = openFlights.remove(bestIndex);
            int currentParent = openParents.remove(bestIndex);
            double currentCost = openCosts.remove(bestIndex);
            openScores.remove(bestIndex);

            boolean alreadyClosed = false;
            for (int i = 0; i < closedAirports.size(); i++) {
                if (closedAirports.get(i).getAirportID() == currentAirport.getAirportID()) {
                    alreadyClosed = true;
                }
            }
            if (alreadyClosed == true) {
                continue;
            }

            closedAirports.add(currentAirport);
            closedFlights.add(currentFlight);
            closedParents.add(currentParent);
            closedCosts.add(currentCost);
            int currentClosedIndex = closedAirports.size() - 1;

            if (currentAirport.getAirportID() == destination.getAirportID()) {
                List<Flight> result = new ArrayList<>();
                int index = currentClosedIndex;
                while (index != -1) {
                    Flight flightAtIndex = closedFlights.get(index);
                    if (flightAtIndex != null) {
                        result.add(0, flightAtIndex);
                    }
                    index = closedParents.get(index);
                }
                return result;
            }

            for (int i = 0; i < allAirports.size(); i++) {
                Airport neighbour = allAirports.get(i);

                if (neighbour.getAirportID() == currentAirport.getAirportID()) {
                    continue;
                }

                boolean neighbourClosed = false;
                for (int j = 0; j < closedAirports.size(); j++) {
                    if (closedAirports.get(j).getAirportID() == neighbour.getAirportID()) {
                        neighbourClosed = true;
                    }
                }
                if (neighbourClosed == true) {
                    continue;
                }

                Flight nextFlight = flightRepository.findFlight(currentAirport, neighbour);
                if (nextFlight == null) {
                    continue;
                }

                double waitingHours = 0;
                if (currentFlight != null) {
                    if (nextFlight.getDepartureDateTime().isBefore(currentFlight.getArrivalDateTime())) {
                        continue;
                    }
                    long waitingMinutes = Duration.between(currentFlight.getArrivalDateTime(), nextFlight.getDepartureDateTime()).toMinutes();
                    waitingHours = waitingMinutes / 60.0;
                }

                long flyingMinutes = Duration.between(nextFlight.getDepartureDateTime(), nextFlight.getArrivalDateTime()).toMinutes();
                double flyingHours = flyingMinutes / 60.0;

                double newCost = currentCost + waitingHours + flyingHours;
                double newScore = newCost + calculateHeuristic(neighbour, destination);

                openAirports.add(neighbour);
                openFlights.add(nextFlight);
                openParents.add(currentClosedIndex);
                openCosts.add(newCost);
                openScores.add(newScore);
            }
        }

        return null;
    }

    private Route buildRoute(List<Flight> flights) {
        List<Integer> legs = new ArrayList<>();
        for (int i = 0; i < flights.size(); i++) {
            legs.add(flights.get(i).getFlightID());
        }

        Flight firstFlight = flights.get(0);
        Flight lastFlight = flights.get(flights.size() - 1);
        long totalMinutes = Duration.between(firstFlight.getDepartureDateTime(), lastFlight.getArrivalDateTime()).toMinutes();

        Route route = new Route();
        route.setLegs(legs);
        route.setTotalTravelTime(totalMinutes / 60.0);
        return route;
    }

    private double calculateHeuristic(Airport a, Airport b) {
        double latitudeDifference = a.getLatitude() - b.getLatitude();
        double longitudeDifference = a.getLongitude() - b.getLongitude();
        double distance = Math.sqrt(latitudeDifference * latitudeDifference + longitudeDifference * longitudeDifference);
        double estimatedHours = distance / 15.0;
        return estimatedHours;
    }
}