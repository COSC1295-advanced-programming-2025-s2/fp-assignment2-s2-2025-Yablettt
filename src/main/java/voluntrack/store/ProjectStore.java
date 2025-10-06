package main.java.voluntrack.store;

import main.java.voluntrack.model.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProjectStore {

    private static final String CSV_PATH = "/data/projects.csv";

    public static List<Project> loadAll() {
        List<Project> list = new ArrayList<>();

        InputStream in = ProjectStore.class.getResourceAsStream(CSV_PATH);
        if (in == null) {
            throw new RuntimeException("Failed to load projects.csv (not found on classpath at " + CSV_PATH + ")");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;


                if (first) {
                    String lower = line.toLowerCase();
                    if (lower.contains("title") && lower.contains("location")) {
                        first = false;
                        continue;
                    }
                    first = false;
                }

                String[] cols = line.split(",", -1);
                if (cols.length < 6) {
                    throw new RuntimeException("projects.csv row has fewer than 6 columns: " + line);
                }

                String title = cols[0];
                String location = cols[1];
                String day = cols[2];
                double hourlyValue = Double.parseDouble(cols[3]);
                int registeredSlots = Integer.parseInt(cols[4]);
                int totalSlots = Integer.parseInt(cols[5]);

                list.add(new Project(title, location, day, hourlyValue, registeredSlots, totalSlots));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load projects.csv", e);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Failed to parse number in projects.csv: " + nfe.getMessage(), nfe);
        }

        return list;
    }
}
