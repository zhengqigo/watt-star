package org.fuelteam.watt.star.core;

public interface SqlHandler {

    String handler(String sql, Object... args);
}