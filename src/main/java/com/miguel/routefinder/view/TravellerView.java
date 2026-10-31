package com.miguel.routefinder.view;

import com.miguel.routefinder.model.Route;
import com.miguel.routefinder.model.Trip;
import com.miguel.routefinder.repositories.TripRepository;

import java.util.List;
import java.util.Scanner;

public class TravellerView {

    public void displayTravellerMenu() {
        System.out.println();
        System.out.println("1. Find route");
        System.out.println("4. View flight history");
        System.out.println("5. Exit");
    }

    public int getMenuChoice() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose an option: ");
        String text = scanner.nextLine();
        try {
            int choice = Integer.parseInt(text.trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String[] getLoginCredentials() {
        Scanner scanner = new Scanner(System.in);
        String[] credentials = new String[2];
        System.out.print("Email: ");
        credentials[0] = scanner.nextLine();
        System.out.print("Password: ");
        credentials[1] = scanner.nextLine();
        return credentials;
    }

    public String[] getSignupCredentials() {
        Scanner scanner = new Scanner(System.in);
        String[] credentials = new String[3];
        System.out.print("Email: ");
        credentials[0] = scanner.nextLine();
        System.out.print("Password: ");
        credentials[1] = scanner.nextLine();
        System.out.print("Confirm password: ");
        credentials[2] = scanner.nextLine();
        return credentials;
    }

    public void displayRoute(Route route) {
        if (route == null) {
            System.out.println("No route found.");
            return;
        }
        System.out.println("Route found.");
        for (int i = 0; i < route.getLegs().size(); i++) {
            System.out.println("Leg " + (i + 1) + ": flight " + route.getLegs().get(i));
        }
        System.out.println("Total travel time: " + route.getTotalTravelTime() + " hours");
    }

    public void displayFlightHistory(int userID) {
        TripRepository tripRepository = new TripRepository();
        List<Trip> trips = tripRepository.getTripsByUser(userID);
        if (trips.size() == 0) {
            System.out.println("You have no trips yet.");
            return;
        }
        System.out.println("Your trips, oldest first:");
        for (int i = 0; i < trips.size(); i++) {
            System.out.println("Trip " + trips.get(i).getTripID());
        }
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }
}