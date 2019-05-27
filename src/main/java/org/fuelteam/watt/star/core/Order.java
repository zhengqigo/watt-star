package org.fuelteam.watt.star.core;

public enum Order {

    // @formatter:off
	AFTER("after"), 
	BEFORE("before");
    // @formatter:on

    private String order;

    private Order(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return order;
    }

    public String getOrder() {
        return order;
    }
}