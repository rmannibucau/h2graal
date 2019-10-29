package com.github.rmannibucau.h2;

import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.stream.IntStream;

public final class Sample {
    public static void main(final String[] args) throws Exception {
        if (args.length > 0) {
            System.out.println("------");
        } else {
            System.out.println("No statement, exiting");
            return;
        }
        try (final Connection connection = getHikariLikeConnection("org.h2.Driver");
             final Statement statement = connection.createStatement()) {
            for (final String stmt : args) {
                System.out.print(stmt + ": ");
                if (stmt.toLowerCase(ROOT).startsWith("select")) {
                    final ResultSet resultSet = statement.executeQuery(stmt);
                    final ResultSetMetaData metaData = resultSet.getMetaData();
                    while (resultSet.next()) {
                        System.out.println(IntStream.range(1, metaData.getColumnCount() + 1)
                                .boxed()
                                .collect(toMap(i -> {
                                    try {
                                        return metaData.getColumnName(i);
                                    } catch (final SQLException e) {
                                        throw new IllegalStateException(e);
                                    }
                                }, i -> {
                                    try {
                                        return resultSet.getObject(i);
                                    } catch (final SQLException e) {
                                        throw new IllegalStateException(e);
                                    }
                                })));
                    }
                } else {
                    System.out.println(statement.execute(stmt));
                }
                System.out.println("------");
            }
        }
    }

    private static Connection getHikariLikeConnection(final String driverClass) throws Exception {
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClass)) {
                return getConnection(driver);
            }
        }
        return getConnection(createDriverInstance(driverClass));
    }

    private static Connection getConnection(final Driver driver) throws Exception {
        final Properties credentials = new Properties() {{
            setProperty("user", "sa");
            setProperty("password", "");
        }};
        return driver.connect("jdbc:h2:mem:demo", credentials);
    }

    private static Driver createDriverInstance(String driverClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Class<? extends Driver> driver = (Class<? extends Driver>) loader.loadClass(driverClass);
        return driver.getConstructor().newInstance();
    }

    private Sample() {
        // no-op
    }
}
