package com.github.rmannibucau.h2;

import static java.util.Collections.emptyEnumeration;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(DriverManager.class)
final class Target_DriverManager { // better than -H:-UseServiceLoaderFeature which breaks other things
    @Substitute // com.github.rmannibucau.h2.Sample.getHikariLikeConnection
    public static Enumeration<Driver> getDrivers() {
        return emptyEnumeration();
    }

    @Substitute // disable autoloading and therefore h2 AutoDriver
    public static synchronized void registerDriver(final java.sql.Driver driver) throws SQLException {
        // no-op
    }
}

public class H2GraalCustomizations {
}
