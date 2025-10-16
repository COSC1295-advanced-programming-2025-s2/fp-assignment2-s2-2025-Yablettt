package main.java.voluntrack.store;

import main.java.voluntrack.model.Registration;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationStore {

    public static List<Registration> loadFor(String username) {
        List<Registration> list = new ArrayList<>();
        String sql =
                "SELECT regId, username, projectId, slots, hours, value, dateTime " +
                        "FROM registrations " +
                        "WHERE username = ? " +
                        "ORDER BY dateTime DESC";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Registration(
                            rs.getInt("regId"),
                            rs.getString("username"),
                            rs.getInt("projectId"),
                            rs.getInt("slots"),
                            rs.getInt("hours"),
                            rs.getDouble("value"),
                            rs.getString("dateTime")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load registrations for " + username, e);
        }
        return list;
    }

    public static boolean addRegistration(String username, String projectTitle,
                                          int slots, int hours, double value) {
        if (hours < 1 || hours > 3 || slots < 1 || slots > 3) return false;

        try (Connection conn = Database.connect()) {
            // get project by title
            int projectId = -1, totalSlots = 0, regSlots = 0;
            String location = "", day = "";
            double hourlyValue = 0.0;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, location, day, hourlyValue, registeredSlots, totalSlots " +
                            "FROM projects WHERE title = ?")) {
                ps.setString(1, projectTitle);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        projectId = rs.getInt("id");
                        location = rs.getString("location");
                        day = rs.getString("day");
                        hourlyValue = rs.getDouble("hourlyValue");
                        regSlots = rs.getInt("registeredSlots");
                        totalSlots = rs.getInt("totalSlots");
                    } else {
                        return false;
                    }
                }
            }

            // will help deny if day has passed in the week
            if (!isDayAllowed(day)) return false;

            int available = Math.max(0, totalSlots - regSlots);
            if (slots > available) return false;

            String now = LocalDateTime.now().toString();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO registrations (username, projectId, slots, hours, value, dateTime) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, username);
                ps.setInt(2, projectId);
                ps.setInt(3, slots);
                ps.setInt(4, hours);
                ps.setDouble(5, value == 0.0 ? hourlyValue * hours * slots : value);
                ps.setString(6, now);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE projects SET registeredSlots = registeredSlots + ? WHERE id = ?")) {
                ps.setInt(1, slots);
                ps.setInt(2, projectId);
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static boolean isDayAllowed(String projectDayName) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        int todayIdx = dayToIndex(today.name());
        int projIdx  = dayToIndex(projectDayName.toUpperCase());
        if (projIdx == -1) return false;
        return projIdx >= todayIdx;
    }

    private static int dayToIndex(String upper) {
        switch (upper) {
            case "MONDAY": return 1;
            case "TUESDAY": return 2;
            case "WEDNESDAY": return 3;
            case "THURSDAY": return 4;
            case "FRIDAY": return 5;
            case "SATURDAY": return 6;
            case "SUNDAY": return 7;
            default: return -1;
        }
    }
}
