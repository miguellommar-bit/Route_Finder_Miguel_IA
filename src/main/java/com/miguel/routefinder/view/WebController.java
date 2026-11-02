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

    private RouteFinderController finder = new RouteFinderController();
    private AirportRepository airportdb = new AirportRepository();
    private FlightRepository flightdb = new FlightRepository();
    private TripRepository tripdb = new TripRepository();
    private UserRepository userdb = new UserRepository();

    @GetMapping({"/", "/login"})
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session, Model model) {
        AuthController auth = new AuthController();
        boolean ok = auth.login(email, password);

        if (ok == false) {
            model.addAttribute("message", auth.getMessage());
            return "login";
        }

        User user = userdb.getUserByEmail(email);
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
                         @RequestParam("confirmPassword") String confirm,
                         Model model) {
        AuthController auth = new AuthController();
        boolean ok = auth.createAccount(email, password, confirm);

        if (ok == true) {
            model.addAttribute("message", auth.getMessage() + " You can now log in.");
            return "login";
        }

        model.addAttribute("message", auth.getMessage());
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
    public String searchRoute(@RequestParam("origin") String origin,
                              @RequestParam("destination") String destination,
                              HttpSession session, Model model) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }

        Airport start = findAirport(origin);
        Airport end = findAirport(destination);

        if (start == null) {
            model.addAttribute("message", "Invalid origin airport: " + origin);
            return "route";
        }
        if (end == null) {
            model.addAttribute("message", "Invalid destination airport: " + destination);
            return "route";
        }

        Route route = finder.findRoute(start, end);

        if (route == null) {
            model.addAttribute("message", "No route found between these airports.");
            return "route";
        }

        List<Integer> legs = route.getLegs();
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < legs.size(); i++) {
            String line = describeFlight(legs.get(i));
            lines.add(line);
        }

        double hours = Math.round(route.getTotalTravelTime() * 10) / 10.0;

        model.addAttribute("legs", lines);
        model.addAttribute("totalTime", hours);
        model.addAttribute("originName", start.getName());
        model.addAttribute("destinationName", end.getName());
        return "route";
    }

    @PostMapping("/buy")
    public String buyTickets(@RequestParam("origin") String origin,
                             @RequestParam("destination") String destination,
                             HttpSession session, Model model) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }

        Airport start = findAirport(origin);
        Airport end = findAirport(destination);

        if (start == null) {
            model.addAttribute("message", "Invalid origin airport.");
            return "route";
        }
        if (end == null) {
            model.addAttribute("message", "Invalid destination airport.");
            return "route";
        }

        Route route = finder.findRoute(start, end);

        if (route == null) {
            model.addAttribute("message", "This route is no longer available.");
            return "route";
        }

        Integer boxed = (Integer) session.getAttribute("userID");
        int id = boxed;
        int trip = tripdb.insertTrip(id);

        if (trip == -1) {
            model.addAttribute("message", "The purchase failed. Please try again.");
            return "route";
        }

        List<Integer> legs = route.getLegs();
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < legs.size(); i++) {
            tripdb.insertTripItinerary(trip, legs.get(i), i + 1);
            String line = describeFlight(legs.get(i));
            lines.add(line);
        }

        double hours = Math.round(route.getTotalTravelTime() * 10) / 10.0;

        model.addAttribute("tripID", trip);
        model.addAttribute("legs", lines);
        model.addAttribute("totalTime", hours);
        return "bought";
    }

    @GetMapping("/history")
    public String showHistory(HttpSession session, Model model) {
        if (isLoggedIn(session) == false) {
            return "redirect:/login";
        }

        Integer boxed = (Integer) session.getAttribute("userID");
        int id = boxed;
        List<Trip> trips = tripdb.getTripsByUser(id);
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            List<Integer> ids = tripdb.getFlightIDsByTrip(trip.getTripID());
            String line = "Trip " + trip.getTripID() + ": ";

            for (int j = 0; j < ids.size(); j++) {
                line = line + describeFlight(ids.get(j));
                if (j < ids.size() - 1) {
                    line = line + "  THEN  ";
                }
            }
            lines.add(line);
        }

        model.addAttribute("trips", lines);
        return "history";
    }

    @GetMapping("/admin/flight")
    public String showEditFlight(HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }
        loadFlightTables(model);
        return "edit-flight";
    }

    @PostMapping("/admin/flight")
    public String editFlight(@RequestParam("flightID") String flighttext,
                             @RequestParam("originID") String fromtext,
                             @RequestParam("destinationID") String totext,
                             @RequestParam("departure") String leavetext,
                             @RequestParam("arrival") String landtext,
                             HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }

        String message = "";

        try {
            int flightid = Integer.parseInt(flighttext.trim());
            int fromid = Integer.parseInt(fromtext.trim());
            int toid = Integer.parseInt(totext.trim());
            LocalDateTime leave = LocalDateTime.parse(leavetext.trim());
            LocalDateTime land = LocalDateTime.parse(landtext.trim());

            if (flightdb.getFlight(flightid) == null) {
                message = "No flight exists with that ID.";
            } else if (airportdb.getAirport(fromid) == null) {
                message = "Invalid origin airport ID.";
            } else if (airportdb.getAirport(toid) == null) {
                message = "Invalid destination airport ID.";
            } else if (land.isBefore(leave)) {
                message = "Arrival must be after departure.";
            } else {
                Flight flight = new Flight();
                flight.setOriginAirportID(fromid);
                flight.setDestinationAirportID(toid);
                flight.setDepartureDateTime(leave);
                flight.setArrivalDateTime(land);

                boolean ok = flightdb.updateFlight(flightid, flight);
                if (ok == true) {
                    message = "Flight " + flightid + " updated.";
                } else {
                    message = "Flight could not be updated.";
                }
            }

        } catch (Exception e) {
            message = "Invalid input. Check the numbers and the dates.";
        }

        model.addAttribute("message", message);
        loadFlightTables(model);
        return "edit-flight";
    }

    @GetMapping("/admin/airport")
    public String showEditAirport(HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }
        loadAirportTable(model);
        return "edit-airport";
    }

    @PostMapping("/admin/airport")
    public String editAirport(@RequestParam("airportID") String idtext,
                              @RequestParam("name") String name,
                              @RequestParam("city") String city,
                              @RequestParam("country") String country,
                              @RequestParam("latitude") String lattext,
                              @RequestParam("longitude") String lontext,
                              HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }

        String message = "";

        try {
            int id = Integer.parseInt(idtext.trim());
            double lat = Double.parseDouble(lattext.trim());
            double lon = Double.parseDouble(lontext.trim());

            if (airportdb.getAirport(id) == null) {
                message = "No airport exists with that ID.";
            } else {
                Airport airport = new Airport();
                airport.setName(name);
                airport.setCity(city);
                airport.setCountry(country);
                airport.setLatitude(lat);
                airport.setLongitude(lon);

                boolean ok = airportdb.updateAirport(id, airport);
                if (ok == true) {
                    message = "Airport " + id + " updated.";
                } else {
                    message = "Airport could not be updated.";
                }
            }

        } catch (Exception e) {
            message = "Invalid input. Check the numbers.";
        }

        model.addAttribute("message", message);
        loadAirportTable(model);
        return "edit-airport";
    }

    @GetMapping("/admin/users")
    public String showUsers(HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }
        loadUserTable(model);
        return "edit-users";
    }

    @PostMapping("/admin/users")
    public String makeAdmin(@RequestParam("userID") String idtext,
                            HttpSession session, Model model) {
        if (isAdmin(session) == false) {
            return "redirect:/menu";
        }

        String message = "";

        try {
            int id = Integer.parseInt(idtext.trim());
            boolean ok = userdb.updateUserRole(id, "admin");

            if (ok == true) {
                message = "User " + id + " is now an admin.";
            } else {
                message = "That user could not be updated.";
            }

        } catch (Exception e) {
            message = "Invalid user ID.";
        }

        model.addAttribute("message", message);
        loadUserTable(model);
        return "edit-users";
    }

    private void loadFlightTables(Model model) {
        model.addAttribute("flights", flightdb.getAllFlights());
        model.addAttribute("airports", airportdb.getAllAirports());
    }

    private void loadAirportTable(Model model) {
        model.addAttribute("airports", airportdb.getAllAirports());
    }

    private void loadUserTable(Model model) {
        model.addAttribute("users", userdb.getAllUsers());
    }

    private boolean isLoggedIn(HttpSession session) {
        if (session.getAttribute("userID") == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) {
            return false;
        }
        if (role.equals("admin")) {
            return true;
        } else {
            return false;
        }
    }

    private Airport findAirport(String text) {
        if (text == null) {
            return null;
        }

        String word = text.trim().toLowerCase();
        int id = 1;
        Airport airport = airportdb.getAirport(id);

        while (airport != null) {
            String name = airport.getName().toLowerCase();
            String city = airport.getCity().toLowerCase();
            if (name.equals(word) || city.equals(word)) {
                return airport;
            }
            id = id + 1;
            airport = airportdb.getAirport(id);
        }
        return null;
    }

    private String describeFlight(int id) {
        Flight flight = flightdb.getFlight(id);
        if (flight == null) {
            return "Unknown flight";
        }

        Airport from = airportdb.getAirport(flight.getOriginAirportID());
        Airport to = airportdb.getAirport(flight.getDestinationAirportID());
        String leaves = flight.getDepartureDateTime().toString();
        String lands = flight.getArrivalDateTime().toString();

        return from.getCity() + " to " + to.getCity() + " (departs " + leaves + ", arrives " + lands + ")";
    }
}