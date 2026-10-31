package com.miguel.routefinder.view;

import com.miguel.routefinder.control.AuthController;
import com.miguel.routefinder.control.RouteFinderController;
import com.miguel.routefinder.model.Airport;
import com.miguel.routefinder.model.Flight;
import com.miguel.routefinder.model.Route;
import com.miguel.routefinder.model.Trip;
import com.miguel.routefinder.model.User;
import com.miguel.routefinder.repositories.AirportRepository;
import com.miguel.routefinder.repositories.FlightRepository;
import com.miguel.routefinder.repositories.TripRepository;
import com.miguel.routefinder.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    private RouteFinderController routeFinderController = new RouteFinderController();
    private AirportRepository airportRepository = new AirportRepository();
    private FlightRepository flightRepository = new FlightRepository();
    private TripRepository tripRepository = new TripRepository();
    private UserRepository userRepository = new UserRepository();

    @GetMapping({"/", "/login"})
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session, Model model) {
        AuthController authController = new AuthController();
        boolean success = authController.login(email, password);

        if (success == false) {
            model.addAttribute("message", authController.getMessage());
            return "login";
        }

        User user = userRepository.getUserByEmail(email);
        session.setAttribute("userID", user.getUserID());
        session.setAttribute("role", user.getRole());
        return "redirect:/menu";
    }

    @GetMapping("/signup")
    public String showSignup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("email") String email,
                         @RequestParam("password") String password,
                         @RequestParam("confirmPassword") String confirmPassword,
                         Model model) {
        AuthController authController = new AuthController();
        boolean success = authController.createAccount(email, password, confirmPassword);

        if (success == true) {
            model.addAttribute("message", authController.getMessage() + " You can now log in.");
            return "login";
        }

        model.addAttribute("message", authController.getMessage());
        return "signup";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }
        model.addAttribute("isAdmin", isAdmin(session));
        return "menu";
    }

    @GetMapping("/route")
    public String showRouteForm(HttpSession session) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }
        return "route";
    }

    @PostMapping("/route")
    public String searchRoute(@RequestParam("origin") String originText,
                              @RequestParam("destination") String destinationText,
                              HttpSession session, Model model) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }

        Airport origin = findAirport(originText);
        Airport destination = findAirport(destinationText);

        if (origin == null) {
            model.addAttribute("message", "Invalid origin airport: " + originText);
            return "route";
        }
        if (destination == null) {
            model.addAttribute("message", "Invalid destination airport: " + destinationText);
            return "route";
        }

        Route route = routeFinderController.findRoute(origin, destination);

        if (route == null) {
            model.addAttribute("message", "No route found between these airports.");
            return "route";
        }

        int userID = (Integer) session.getAttribute("userID");
        int tripID = tripRepository.insertTrip(userID);
        List<Integer> legs = route.getLegs();
        List<String> legLines = new ArrayList<>();

        for (int i = 0; i < legs.size(); i++) {
            tripRepository.insertTripItinerary(tripID, legs.get(i), i + 1);
            legLines.add(describeFlight(legs.get(i)));
        }

        model.addAttribute("legs", legLines);
        model.addAttribute("totalTime", String.format("%.1f", route.getTotalTravelTime()));
        return "route";
    }

    @GetMapping("/history")
    public String showHistory(HttpSession session, Model model) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }

        int userID = (Integer) session.getAttribute("userID");
        List<Trip> trips = tripRepository.getTripsByUser(userID);
        List<String> tripLines = new ArrayList<>();

        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            List<Integer> flightIDs = tripRepository.getFlightIDsByTrip(trip.getTripID());
            String line = "Trip " + trip.getTripID() + ": ";

            for (int j = 0; j < flightIDs.size(); j++) {
                line = line + describeFlight(flightIDs.get(j));
                if (j < flightIDs.size() - 1) {
                    line = line + "  THEN  ";
                }
            }
            tripLines.add(line);
        }

        model.addAttribute("trips", tripLines);
        return "history";
    }

    @GetMapping("/admin/flight")
    public String showEditFlight(HttpSession session) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }
        return "edit-flight";
    }

    @PostMapping("/admin/flight")
    public String editFlight(@RequestParam("flightID") String flightIDText,
                             @RequestParam("originID") String originIDText,
                             @RequestParam("destinationID") String destinationIDText,
                             @RequestParam("departure") String departureText,
                             @RequestParam("arrival") String arrivalText,
                             HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }

        try {
            int flightID = Integer.parseInt(flightIDText.trim());
            int originID = Integer.parseInt(originIDText.trim());
            int destinationID = Integer.parseInt(destinationIDText.trim());
            LocalDateTime departure = LocalDateTime.parse(departureText.trim());
            LocalDateTime arrival = LocalDateTime.parse(arrivalText.trim());

            if (flightRepository.getFlight(flightID) == null) {
                model.addAttribute("message", "No flight exists with that ID.");
                return "edit-flight";
            }
            if (airportRepository.getAirport(originID) == null || airportRepository.getAirport(destinationID) == null) {
                model.addAttribute("message", "Invalid airport ID.");
                return "edit-flight";
            }
            if (arrival.isBefore(departure)) {
                model.addAttribute("message", "Arrival must be after departure.");
                return "edit-flight";
            }

            Flight newFlight = new Flight();
            newFlight.setOriginAirportID(originID);
            newFlight.setDestinationAirportID(destinationID);
            newFlight.setDepartureDateTime(departure);
            newFlight.setArrivalDateTime(arrival);

            boolean updated = flightRepository.updateFlight(flightID, newFlight);
            if (updated == true) {
                model.addAttribute("message", "Flight updated.");
            } else {
                model.addAttribute("message", "Flight could not be updated.");
            }

        } catch (Exception e) {
            model.addAttribute("message", "Invalid input. Check the numbers and the dates.");
        }
        return "edit-flight";
    }

    @GetMapping("/admin/airport")
    public String showEditAirport(HttpSession session) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }
        return "edit-airport";
    }

    @PostMapping("/admin/airport")
    public String editAirport(@RequestParam("airportID") String airportIDText,
                              @RequestParam("name") String name,
                              @RequestParam("city") String city,
                              @RequestParam("country") String country,
                              @RequestParam("latitude") String latitudeText,
                              @RequestParam("longitude") String longitudeText,
                              HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }

        try {
            int airportID = Integer.parseInt(airportIDText.trim());
            double latitude = Double.parseDouble(latitudeText.trim());
            double longitude = Double.parseDouble(longitudeText.trim());

            if (airportRepository.getAirport(airportID) == null) {
                model.addAttribute("message", "No airport exists with that ID.");
                return "edit-airport";
            }

            Airport newAirport = new Airport();
            newAirport.setName(name);
            newAirport.setCity(city);
            newAirport.setCountry(country);
            newAirport.setLatitude(latitude);
            newAirport.setLongitude(longitude);

            boolean updated = airportRepository.updateAirport(airportID, newAirport);
            if (updated == true) {
                model.addAttribute("message", "Airport updated.");
            } else {
                model.addAttribute("message", "Airport could not be updated.");
            }

        } catch (Exception e) {
            model.addAttribute("message", "Invalid input. Check the numbers.");
        }
        return "edit-airport";
    }

    private boolean isLoggedIn(HttpSession session) {
        if (session.getAttribute("userID") == null) {
            return false;
        }
        return true;
    }

    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) {
            return false;
        }
        return role.equals("admin");
    }

    private Airport findAirport(String text) {
        if (text == null) {
            return null;
        }
        String wanted = text.trim().toLowerCase();
        int id = 1;
        Airport airport = airportRepository.getAirport(id);

        while (airport != null) {
            if (airport.getName().toLowerCase().equals(wanted)) {
                return airport;
            }
            if (airport.getCity().toLowerCase().equals(wanted)) {
                return airport;
            }
            id = id + 1;
            airport = airportRepository.getAirport(id);
        }
        return null;
    }

    private String describeFlight(int flightID) {
        Flight flight = flightRepository.getFlight(flightID);
        if (flight == null) {
            return "Unknown flight";
        }
        Airport from = airportRepository.getAirport(flight.getOriginAirportID());
        Airport to = airportRepository.getAirport(flight.getDestinationAirportID());
        String departs = flight.getDepartureDateTime().toString().replace("T", " ");
        String arrives = flight.getArrivalDateTime().toString().replace("T", " ");
        return from.getCity() + " to " + to.getCity() + " (departs " + departs + ", arrives " + arrives + ")";
    }
}