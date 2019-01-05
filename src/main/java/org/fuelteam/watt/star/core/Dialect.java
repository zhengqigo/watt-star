package org.fuelteam.watt.star.core;

import com.alibaba.druid.util.StringUtils;

public enum Dialect {

    Mysql, Oracle, MariaDB, Other;

    public final static Dialect of(String name) {
        if (name == null || StringUtils.isEmpty(name)) return Dialect.Other;
        for (Dialect dialect : Dialect.values()) {
            if (dialect.name().toLowerCase().equals(name.toLowerCase())) return dialect;
        }
        return Dialect.Other;
    }
}
