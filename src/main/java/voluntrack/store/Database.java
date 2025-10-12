package main.java.voluntrack.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:voluntrack.db";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found on classpath", e);
        }
        return DriverManager.getConnection(URL);
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
                    title TEXT,
                    location TEXT,
                    day TEXT,
                    hourlyValue REAL,
                    registeredSlots INTEGER,
                    totalSlots INTEGER
                );
            """);

            // rego table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS registrations (
                    regId INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT,
                    projectId INTEGER,
                    slots INTEGER,
                    hours INTEGER,
                    value REAL,
                    dateTime TEXT
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

