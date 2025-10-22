package main.java.voluntrack.store;

import main.java.voluntrack.model.User;

import java.security.MessageDigest;
import java.sql.*;

public class UserStore {

    // adds new user to db and hash pwd
    public static void add(User u) {
        String sql = "INSERT INTO users(fullName, username, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getEmail());
            ps.setString(4, hash(u.getPassword()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("User insert failed", e);
        }
    }

    // finds user thru their username and pass and pass is compared used SHA-256 hash
    public static User findUser(String username, String password) {
        String sql = "SELECT fullName, username, email, password FROM users WHERE username = ? AND password = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("fullName"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("User lookup failed", e);
        }
        return null;
    }

    //hashes new pass before storing it
    public static boolean updatePassword(String username, String newRawPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sha256(newRawPassword));
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // reference: asked chatgpt how to create method to hash something that will need to be stored in a db and used later to login
    private static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash error", e);
        }
    }

    // checks if username already exists
    public static boolean exists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // helper that was also given to me when i asked chatgpt how to hash
    public static String sha256(String text) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // validates login attempt
    public static boolean verify(String username, String rawPassword) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String dbHash = rs.getString("password");
                return dbHash.equals(sha256(rawPassword));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
