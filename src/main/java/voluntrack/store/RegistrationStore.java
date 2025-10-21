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
        if (hours < 1 || hours > 3 || slots < 1 || slots > 3) {
            return false;
        }

        try (Connection conn = Database.connect()) {
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

            final boolean BYPASS_DAY_RULE = false;
            if (!BYPASS_DAY_RULE && !isDayAllowed(day)) {
                return false;
            }

            int available = Math.max(0, totalSlots - regSlots);
            if (slots > available) {
                return false;
            }

            String now = java.time.LocalDateTime.now().toString();

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO registrations (username, projectId, slots, hours, value, dateTime) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, username);
                ps.setInt(2, projectId);
                ps.setInt(3, slots);
                ps.setInt(4, hours);
                ps.setDouble(5, value == 0.0 ? hourlyValue * hours * slots : value);
                ps.setString(6, now);
                int rows = ps.executeUpdate();
                if (rows == 0) return false;
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
        if (projectDayName == null) return false;
        int todayIdx = dayToIndex(LocalDate.now().getDayOfWeek().name());
        int projIdx = dayToIndex(projectDayName.trim().toUpperCase());


        return projIdx != -1 && projIdx >= todayIdx;
    }

    private static int dayToIndex(String day) {
        if (day == null) return -1;
        String d = day.trim().toUpperCase();

        if (d.equals("MONDAY"))    return 1;
        if (d.equals("TUESDAY"))   return 2;
        if (d.equals("WEDNESDAY")) return 3;
        if (d.equals("THURSDAY"))  return 4;
        if (d.equals("FRIDAY"))    return 5;
        if (d.equals("SATURDAY"))  return 6;
        if (d.equals("SUNDAY"))    return 7;

        if (d.startsWith("MON")) return 1;
        if (d.startsWith("TUE")) return 2;
        if (d.startsWith("WED")) return 3;
        if (d.startsWith("THU")) return 4;
        if (d.startsWith("FRI")) return 5;
        if (d.startsWith("SAT")) return 6;
        if (d.startsWith("SUN")) return 7;

        return -1;
    }

    public static class HistoryRow {
        private final int regId;
        private final String dateTime;
        private final String title;
        private final String location;
        private final String day;
        private final int slots;
        private final int hours;
        private final double total;

        public HistoryRow(int regId, String dateTime, String title, String location,
                          String day, int slots, int hours, double total) {
            this.regId = regId;
            this.dateTime = dateTime;
            this.title = title;
            this.location = location;
            this.day = day;
            this.slots = slots;
            this.hours = hours;
            this.total = total;
        }

        public int getRegId() {
            return regId;
        }

        public String getDateTime() {
            return dateTime;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation() {
            return location;
        }

        public String getDay() {
            return day;
        }

        public int getSlots() {
            return slots;
        }

        public int getHours() {
            return hours;
        }

        public double getTotal() {
            return total;
        }
    }

    public static List<HistoryRow> loadHistoryRowsFor(String username) {
        List<HistoryRow> list = new ArrayList<>();
        String sql = """
            SELECT r.regId, r.dateTime, r.slots, r.hours, r.value,
                   p.title, p.location, p.day
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
                    list.add(new HistoryRow(
                            rs.getInt("regId"),
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
            throw new RuntimeException("Failed to load history for " + username, e);
        }
        return list;
    }

}

