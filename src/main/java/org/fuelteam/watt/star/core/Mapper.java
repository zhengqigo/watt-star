package org.fuelteam.watt.star.core;

import io.shardingjdbc.core.constant.DatabaseType;

public enum Mapper {

    // @formatter:off
	DEFAULT(tk.mybatis.mapper.common.Mapper.class.getName()),
	MYSQL(tk.mybatis.mapper.common.MySqlMapper.class.getName() + "," + DEFAULT.mapper), 
	MSSQL(tk.mybatis.mapper.common.SqlServerMapper.class.getName() + "," + DEFAULT.mapper);
    // @formatter:on

    private String mapper;

    private Mapper(String mapper) {
        this.mapper = mapper;
    }

    @Override
    public String toString() {
        return mapper;
    }

    public static final Mapper valueOfDialect(DatabaseType databaseType) {
        switch (databaseType) {
            case MySQL:
                return Mapper.MYSQL;
            case SQLServer:
                return Mapper.MSSQL;
            default:
                return Mapper.DEFAULT;
        }
    }

    public String getMapper() {
        return mapper;
    }
}