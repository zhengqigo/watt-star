package org.fuelteam.watt.star.core;

public abstract class Proxy {

    public static <T> T slave(SwitchExecute<T> runnable) {
        DynamicDataSource.useSlave();
        return execute(runnable);
    }

    public static <T> T master(SwitchExecute<T> runnable) {
        DynamicDataSource.useMaster();
        return execute(runnable);
    }

    private static <T> T execute(SwitchExecute<T> runnable) {
        try {
            return runnable.run();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        } finally {
            DynamicDataSource.reset();
        }
    }

    public static interface SwitchExecute<T> {
        T run() throws Throwable;
    }
}
