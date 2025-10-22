package main.java.voluntrack.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class CartStore {

    public static class Item {
        private final String title;
        private final double hourlyValue;
        private final int slots;
        private final int hours;

        // holds project info and calc total cost
        public Item(String title, double hourlyValue, int slots, int hours) {
            this.title = title;
            this.hourlyValue = hourlyValue;
            this.slots = slots;
            this.hours = hours;
        }
        public String getTitle() { return title; }
        public double getHourlyValue() { return hourlyValue; }
        public int getSlots() { return slots; }
        public int getHours() { return hours; }
        public double getTotal() { return hourlyValue * slots * hours; }
    }

    // maps username to observable list of cart items
    private static final Map<String, ObservableList<Item>> carts = new HashMap<>();


    // returns cart
    public static ObservableList<Item> getCart(String username) {
        return carts.computeIfAbsent(username, k -> FXCollections.observableArrayList());
    }

    public static void add(String username, String title, double hourlyValue, int slots, int hours) {
        getCart(username).add(new Item(title, hourlyValue, slots, hours));
    }

    public static void removeAt(String username, int index) {
        var list = getCart(username);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
        }
    }

    public static void clear(String username) {
        getCart(username).clear();
    }
}
