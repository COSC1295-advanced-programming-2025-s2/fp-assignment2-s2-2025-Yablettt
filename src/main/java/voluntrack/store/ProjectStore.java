package main.java.voluntrack.store;

import main.java.voluntrack.model.Project;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectStore {

    public static List<Project> loadAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(new Project(
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getDouble("hourlyValue"),
                        rs.getInt("registeredSlots"),
                        rs.getInt("totalSlots")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
}
