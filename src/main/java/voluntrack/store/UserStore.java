package main.java.voluntrack.store;

import main.java.voluntrack.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserStore {
    private static final List<User> users = new ArrayList<>();

    public static void addUser(User user) {
        users.add(user);
    }

    public static User findUser(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}
