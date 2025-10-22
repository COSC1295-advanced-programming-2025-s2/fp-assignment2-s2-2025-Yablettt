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


    // load projects from db
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

    // seeds db from a csv file on the classpath
    // reference: asked chatgpt how to connect with database using contents from a csv
    public static void loadCSV(String resourceCsvPath) {
        final String countSql = "SELECT COUNT(*) FROM projects";
        try (Connection c = Database.connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(countSql)) {

            int count = rs.next() ? rs.getInt(1) : 0;
            if (count > 0) {
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check projects count", e);
        }

        var in = ProjectStore.class.getResourceAsStream(resourceCsvPath);
        if (in == null) {
            throw new RuntimeException("CSV not found on classpath: ");
        }

        try (java.io.BufferedReader br =
                     new java.io.BufferedReader(new java.io.InputStreamReader(in))) {

            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!headerSkipped) {
                    if (line.toLowerCase().contains("title") ||
                            line.toLowerCase().contains("hourly") ||
                            line.toLowerCase().contains("location")) {
                        headerSkipped = true;
                        continue;
                    }
                    headerSkipped = true;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 6) {
                    continue;
                }

                String title    = parts[0].trim();
                String location = parts[1].trim();
                String day      = parts[2].trim();

                String hvRaw = parts[3].replace("$", "").replace("AUD", "").trim();
                double hourlyValue = Double.parseDouble(hvRaw);

                int registeredSlots = safeParseInt(parts[4].trim(), 0);
                int totalSlots      = safeParseInt(parts[5].trim(), 0);

                boolean enabled = true;
                if (parts.length >= 7) {
                    String e = parts[6].trim().toLowerCase();
                    enabled = !(e.equals("0") || e.equals("false") || e.equals("no"));
                }

                boolean ok = insert(title, location, day, hourlyValue, registeredSlots, totalSlots);
                if (!ok) {
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to seed projects from CSV", ex);
        }
    }

    private static int safeParseInt(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (Exception ignored) { return fallback; }
    }
}
