package com.example.icecreamtruckv2.money;

public class MoneyItem {
    private boolean done;
    private String title;
    private int cost;
    private long timestamp;

    public MoneyItem() {
        // empty constructor
    }

    public MoneyItem(String t, int c, boolean d, long ts) {
        title = t;
        cost = c;
        done = d;
        timestamp = ts;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

