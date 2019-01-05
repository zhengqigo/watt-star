package org.fuelteam.watt.star.core;

public enum Order {

    AFTER("after"), BEFORE("before");

    private String order;

    private Order(String order) {
        this.order = order;
    }

    public String getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return order;
    }
}