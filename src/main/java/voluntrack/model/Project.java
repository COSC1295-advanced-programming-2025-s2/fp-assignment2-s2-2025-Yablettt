package main.java.voluntrack.model;

public class Project {
    private String title;
    private String location;
    private String day;
    private double hourlyValue;
    private int registeredSlots;
    private int totalSlots;
    private boolean enabled;

    public Project(String title, String location, String day, double hourlyValue, int registeredSlots, int totalSlots, boolean enabled) {
        this.title = title;
        this.location = location;
        this.day = day;
        this.hourlyValue = hourlyValue;
        this.registeredSlots = registeredSlots;
        this.totalSlots = totalSlots;
        this.enabled = enabled;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDay() { return day; }
    public double getHourlyValue() { return hourlyValue; }
    public int getAvailableSlots() { return Math.max(0, totalSlots - registeredSlots); }

}
