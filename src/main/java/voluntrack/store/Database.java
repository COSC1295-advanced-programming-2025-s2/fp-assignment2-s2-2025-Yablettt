package main.java.voluntrack.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:voluntrack.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        try (Connection conn = connect();
             var stmt = conn.createStatement()) {

            // USERS table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fullName TEXT NOT NULL,
                    username TEXT UNIQUE NOT NULL,
                    email TEXT,
                    password TEXT NOT NULL
                );
            """);

            // PROJECTS table
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

            // REGISTRATIONS table
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
    }
}

