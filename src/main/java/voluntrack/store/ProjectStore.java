package main.java.voluntrack.store;

import main.java.voluntrack.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectStore {

    public static List<Project> loadAll() {
        String sql = """
            SELECT title, location, day, hourlyValue, registeredSlots, totalSlots, enabled
            FROM projects
            ORDER BY title
        """;
        List<Project> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Project(
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getDouble("hourlyValue"),
                        rs.getInt("registeredSlots"),
                        rs.getInt("totalSlots"),
                        rs.getInt("enabled") != 0
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Projects load failed", e);
        }
        return list;
    }

    public static List<Project> loadEnabledOnly() {
        String sql = """
            SELECT title, location, day, hourlyValue, registeredSlots, totalSlots, enabled
            FROM projects
            WHERE enabled = 1
            ORDER BY title
        """;
        List<Project> list = new ArrayList<>();
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Project(
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getDouble("hourlyValue"),
                        rs.getInt("registeredSlots"),
                        rs.getInt("totalSlots"),
                        rs.getInt("enabled") != 0
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Enabled projects load failed", e);
        }
        return list;
    }

    public static boolean insert(String title, String location, String day,
                                 double hourlyValue, int registeredSlots, int totalSlots,
                                 boolean enabled) {
        String sql = """
            INSERT INTO projects (title, location, day, hourlyValue, registeredSlots, totalSlots, enabled)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, location);
            ps.setString(3, day);
            ps.setDouble(4, hourlyValue);
            ps.setInt(5, registeredSlots);
            ps.setInt(6, totalSlots);
            ps.setInt(7, enabled ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Insert project failed", e);
        }
    }

    public static boolean updateProject(String oldTitle, String newTitle, String newLocation,
                                        String newDay, double newHourlyValue, int newTotalSlots) {
        String sql = """
            UPDATE projects
            SET title = ?, location = ?, day = ?, hourlyValue = ?, totalSlots = ?
            WHERE title = ?
        """;
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newTitle);
            ps.setString(2, newLocation);
            ps.setString(3, newDay);
            ps.setDouble(4, newHourlyValue);
            ps.setInt(5, newTotalSlots);
            ps.setString(6, oldTitle);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update project", e);
        }
    }

    public static boolean setEnabled(String title, boolean enabled) {
        String sql = "UPDATE projects SET enabled = ? WHERE title = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enabled ? 1 : 0);
            ps.setString(2, title);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update project status", e);
        }
    }

    public static boolean addRegisteredSlots(String title, int delta) {
        String sql = """
            UPDATE projects
            SET registeredSlots = registeredSlots + ?
            WHERE title = ?
        """;
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setString(2, title);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add registered slots", e);
        }
    }
    public static void update(Project oldProj, Project newProj) { /* ... */ }
    public static boolean isDisabled(Project project) { return false; }
    public static void setDisabled(Project project, boolean disabled) { /* ... */ }

}
