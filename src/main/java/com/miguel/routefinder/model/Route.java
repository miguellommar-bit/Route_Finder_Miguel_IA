package com.miguel.routefinder.model;

import java.util.List;

public class Route {
    private List<Integer> legs;
    private double totalTravelTime;

    public List<Integer> getLegs() { return legs; }
    public void setLegs(List<Integer> legs) { this.legs = legs; }
    public double getTotalTravelTime() { return totalTravelTime; }
    public void setTotalTravelTime(double totalTravelTime) { this.totalTravelTime = totalTravelTime; }
}