package main.java.voluntrack.store;

import main.java.voluntrack.model.Project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    // overloader
    public static boolean insert(String title, String location, String day,
                                 double hourlyValue, int registeredSlots, int totalSlots) {
        return insert(title, location, day, hourlyValue, registeredSlots, totalSlots, true);
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

    public static Project findById(int projectId) {
        String sql = "SELECT title, location, day, hourlyValue, registeredSlots, totalSlots, enabled " +
                "FROM projects WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Project(
                            rs.getString("title"),
                            rs.getString("location"),
                            rs.getString("day"),
                            rs.getDouble("hourlyValue"),
                            rs.getInt("registeredSlots"),
                            rs.getInt("totalSlots"),
                            rs.getBoolean("enabled")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Project lookup failed for id=" + projectId, e);
        }
        return null;
    }

   /* public static boolean addRegisteredSlots(String title, int delta) {
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

    */

    public static List<Project> loadFromCsv(String resourcePath) {
        List<Project> list = new ArrayList<>();

        try (InputStream in = ProjectStore.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new RuntimeException("CSV resource not found: " + resourcePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                boolean headerHandled = false;

                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;


                    if (!headerHandled) {
                        headerHandled = true;
                        String lower = line.toLowerCase();
                        if (lower.contains("title") && lower.contains("hourly")) {
                            continue;
                        }
                    }


                    String[] t = line.split(",", -1);

                    if (t.length < 6) continue;

                    String title = t[0].trim();
                    String location = t[1].trim();
                    String day = t[2].trim();
                    double hourly = Double.parseDouble(t[3].trim());
                    int registered = Integer.parseInt(t[4].trim());
                    int total = Integer.parseInt(t[5].trim());

                    boolean enabled = true;
                    if (t.length >= 7 && !t[6].trim().isEmpty()) {
                        enabled = Boolean.parseBoolean(t[6].trim());
                    }
                    list.add(new Project(title, location, day, hourly, registered, total, enabled));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV " + resourcePath, e);
        }

        return list;
    }

}
