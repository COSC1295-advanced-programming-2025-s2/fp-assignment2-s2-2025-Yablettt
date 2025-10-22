package main.java.voluntrack.model;

public record Registration(int regId, String username, int projectId, int slots, int hours, double value, String dateTime) {}
