package main.java.voluntrack.store;

import main.java.voluntrack.model.User;

import java.security.MessageDigest;
import java.sql.*;

public class UserStore {

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

    public static boolean updatePassword(String username, String oldPlain, String newPlain) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND password = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash(newPlain));
            ps.setString(2, username);
            ps.setString(3, hash(oldPlain));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

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
}
