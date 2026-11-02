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

    private FlightRepository flightdb = new FlightRepository();
    private AirportRepository airportdb = new AirportRepository();

    public Route findRoute(Airport origin, Airport destination) {
        if (origin == null || destination == null) {
            return null;
        }
        if (origin.getAirportID() == destination.getAirportID()) {
            return null;
        }

        Flight direct = findDirectRoute(origin, destination);
        if (direct != null) {
            List<Flight> list = new ArrayList<>();
            list.add(direct);
            return buildRoute(list);
        }

        List<Flight> flights = findConnectingRoute(origin, destination);
        if (flights == null) {
            return null;
        }
        return buildRoute(flights);
    }

    private Flight findDirectRoute(Airport origin, Airport destination) {
        Flight flight = flightdb.findFlight(origin, destination);
        return flight;
    }

    private List<Flight> findConnectingRoute(Airport origin, Airport destination) {
        List<Airport> all = new ArrayList<>();
        int id = 1;
        Airport loaded = airportdb.getAirport(id);
        while (loaded != null) {
            all.add(loaded);
            id = id + 1;
            loaded = airportdb.getAirport(id);
        }

        List<Airport> openair = new ArrayList<>();
        List<Flight> openfly = new ArrayList<>();
        List<Integer> openfrom = new ArrayList<>();
        List<Double> opencost = new ArrayList<>();
        List<Double> openscore = new ArrayList<>();

        List<Airport> closedair = new ArrayList<>();
        List<Flight> closedfly = new ArrayList<>();
        List<Integer> closedfrom = new ArrayList<>();

        openair.add(origin);
        openfly.add(null);
        openfrom.add(-1);
        opencost.add(0.0);
        openscore.add(calculateHeuristic(origin, destination));

        while (openair.size() > 0) {
            int best = 0;
            for (int i = 1; i < openscore.size(); i++) {
                if (openscore.get(i) < openscore.get(best)) {
                    best = i;
                }
            }

            Airport nowair = openair.remove(best);
            Flight nowfly = openfly.remove(best);
            int nowfrom = openfrom.remove(best);
            double nowcost = opencost.remove(best);
            openscore.remove(best);

            boolean done = false;
            for (int i = 0; i < closedair.size(); i++) {
                if (closedair.get(i).getAirportID() == nowair.getAirportID()) {
                    done = true;
                }
            }
            if (done == true) {
                continue;
            }

            closedair.add(nowair);
            closedfly.add(nowfly);
            closedfrom.add(nowfrom);
            int here = closedair.size() - 1;

            if (nowair.getAirportID() == destination.getAirportID()) {
                List<Flight> result = new ArrayList<>();
                int spot = here;
                while (spot != -1) {
                    Flight item = closedfly.get(spot);
                    if (item != null) {
                        result.add(0, item);
                    }
                    spot = closedfrom.get(spot);
                }
                return result;
            }

            for (int i = 0; i < all.size(); i++) {
                Airport place = all.get(i);

                if (place.getAirportID() == nowair.getAirportID()) {
                    continue;
                }

                boolean shut = false;
                for (int j = 0; j < closedair.size(); j++) {
                    if (closedair.get(j).getAirportID() == place.getAirportID()) {
                        shut = true;
                    }
                }
                if (shut == true) {
                    continue;
                }

                Flight next = flightdb.findFlight(nowair, place);
                if (next == null) {
                    continue;
                }

                double waiting = 0;
                if (nowfly != null) {
                    if (next.getDepartureDateTime().isBefore(nowfly.getArrivalDateTime())) {
                        continue;
                    }
                    long minutes = Duration.between(nowfly.getArrivalDateTime(), next.getDepartureDateTime()).toMinutes();
                    waiting = minutes / 60.0;
                }

                long flymin = Duration.between(next.getDepartureDateTime(), next.getArrivalDateTime()).toMinutes();
                double flying = flymin / 60.0;

                double cost = nowcost + waiting + flying;
                double score = cost + calculateHeuristic(place, destination);

                openair.add(place);
                openfly.add(next);
                openfrom.add(here);
                opencost.add(cost);
                openscore.add(score);
            }
        }

        return null;
    }

    private Route buildRoute(List<Flight> flights) {
        List<Integer> legs = new ArrayList<>();
        for (int i = 0; i < flights.size(); i++) {
            legs.add(flights.get(i).getFlightID());
        }

        Flight first = flights.get(0);
        Flight last = flights.get(flights.size() - 1);
        long minutes = Duration.between(first.getDepartureDateTime(), last.getArrivalDateTime()).toMinutes();

        Route route = new Route();
        route.setLegs(legs);
        route.setTotalTravelTime(minutes / 60.0);
        return route;
    }

    private double calculateHeuristic(Airport a, Airport b) {
        double dlat = a.getLatitude() - b.getLatitude();
        double dlon = a.getLongitude() - b.getLongitude();
        double dist = Math.sqrt(dlat * dlat + dlon * dlon);
        double hours = dist / 15.0;
        return hours;
    }
}