package main.java.voluntrack.store;

import java.util.ArrayList;
import java.util.List;

public class CartStore {

    public static class Item {
        public final String title;
        public final double hourly;
        public final int slots;
        public final int hours;

        public Item(String title, double hourly, int slots, int hours) {
            this.title = title;
            this.hourly = hourly;
            this.slots = slots;
            this.hours = hours;
        }

        public double total() { return hourly * slots * hours; }
    }

    private static final List<Item> items = new ArrayList<>();

    public static void clear() { items.clear(); }

    public static void add(String title, double hourly, int slots, int hours) {
        items.add(new Item(title, hourly, slots, hours));
    }

    public static List<Item> all() {
        return new ArrayList<>(items);
    }
}
