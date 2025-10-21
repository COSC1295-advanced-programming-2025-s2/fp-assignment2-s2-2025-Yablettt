package main.java.voluntrack.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:voluntrack.db";

    public static Connection connect() {
        try {
            return java.sql.DriverManager.getConnection("jdbc:sqlite:voluntrack.db");
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void init() {
        try (Connection conn = connect();
             var stmt = conn.createStatement()) {

            // users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fullName TEXT NOT NULL,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT,
                    password TEXT NOT NULL
                );
            """);

            // projects table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  title TEXT NOT NULL,
                  location TEXT NOT NULL,
                  day TEXT NOT NULL,
                  hourlyValue REAL NOT NULL,
                  registeredSlots INTEGER NOT NULL DEFAULT 0,
                  totalSlots INTEGER NOT NULL,
                  enabled INTEGER NOT NULL DEFAULT 1
              );
            """);

            // rego table
            stmt.execute("""
                   CREATE TABLE IF NOT EXISTS registrations (
                      regId INTEGER PRIMARY KEY AUTOINCREMENT,
                      username TEXT NOT NULL,
                      projectId INTEGER NOT NULL,
                      slots INTEGER NOT NULL,
                      hours INTEGER NOT NULL,
                      value REAL NOT NULL,
                      dateTime TEXT NOT NULL,
                      FOREIGN KEY (username) REFERENCES users(username),
                      FOREIGN KEY (projectId) REFERENCES projects(id)
                  );
            """);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialise database", e);
        }

        // Refernce: I asked ChatGPT how to add another column in sql thru java app
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE projects ADD COLUMN enabled BOOLEAN DEFAULT 1");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                throw new RuntimeException("Failed to add 'enabled' column", e);
            }
        }

    }
}

