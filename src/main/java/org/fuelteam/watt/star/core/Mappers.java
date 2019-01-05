package org.fuelteam.watt.star.core;

public enum Mappers {

    DEFAULT(tk.mybatis.mapper.common.Mapper.class.getName()),
    MYSQL(tk.mybatis.mapper.common.MySqlMapper.class.getName() + "," + DEFAULT.mappers);

    private String mappers;

    private Mappers(String mappers) {
        this.mappers = mappers;
    }

    public String getMappers() {
        return mappers;
    }

    @Override
    public String toString() {
        return mappers;
    }

    public static final Mappers of(Dialect dialect) {
        switch (dialect) {
        case Mysql:
            return Mappers.MYSQL;
        default:
            return Mappers.DEFAULT;
        }
    }
}