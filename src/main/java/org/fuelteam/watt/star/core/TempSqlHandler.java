package org.fuelteam.watt.star.core;

public class TempSqlHandler implements SqlHandler {

    @Override
    public String handler(String sql, Object... args) {
        return sql;
    }
}