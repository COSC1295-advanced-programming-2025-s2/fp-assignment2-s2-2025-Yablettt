package main.java.voluntrack.model;

public record Project(
        String title,
        String location,
        String day,
        double hourlyValue,
        int registeredSlots,
        int totalSlots
) {
    public int availableSlots() {
        return Math.max(0, totalSlots - registeredSlots);
    }
}
