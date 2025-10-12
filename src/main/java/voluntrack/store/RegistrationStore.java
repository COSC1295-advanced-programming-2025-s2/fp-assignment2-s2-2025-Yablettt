package main.java.voluntrack.store;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistrationStore {

    public record RegistrationRow(
            String regId, String dateTime, String title, String location, String day,
            int slots, int hours, double value
    ) {}


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

    public static List<RegistrationRow> listByUser(String username) {
        List<RegistrationRow> rows = new ArrayList<>();
        String sql = """
            SELECT r.regId, r.dateTime, p.title, p.location, p.day, r.slots, r.hours, r.value
            FROM registrations r
            JOIN projects p ON p.id = r.projectId
            WHERE r.username = ?
            ORDER BY r.dateTime DESC
        """;
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String regId = String.format("%04d", rs.getInt("regId"));
                    rows.add(new RegistrationRow(
                            regId,
                            rs.getString("dateTime"),
                            rs.getString("title"),
                            rs.getString("location"),
                            rs.getString("day"),
                            rs.getInt("slots"),
                            rs.getInt("hours"),
                            rs.getDouble("value")
                    ));
                }
            }
        } catch (SQLException e) {
        }
        return rows;
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
