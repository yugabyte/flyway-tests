package com.yugabyte;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static com.yugabyte.Utils.startYBDBCluster;
import static com.yugabyte.Utils.stopYBDBCluster;

public class TestYBLocking {
    static String url = "jdbc:yugabytedb://127.0.0.1:5433/yugabyte";
    Connection conn = null;

    Map<String, Exception> errors = new HashMap();
    @Before
    public void setup() {
        startYBDBCluster();
    }

    @Test
    public void advisoryLockTest() {
        Flyway flyway = Flyway
                .configure()
                .dataSource(url, "yugabyte", "yugabyte")
                .load();
        flyway.baseline();

        int numThreads = 5;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> migrationOperation());
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }
        System.out.println("All threads started");
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ie) {
                Assert.fail("Error during join() for " + i + ": " + ie);
            }
        }
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            errors.forEach((i, e) -> sb.append("Thread " + i + " failed with: " + e + "\n"));
            Assert.fail(sb.toString());
        }
        System.out.println("All threads completed");

        try {
            conn = DriverManager.getConnection(url, "yugabyte", "yugabyte");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select count(*) from flyway_schema_history");
            Assert.assertTrue("Baseline Command returned no row from flyway_schema_history table", rs.next());
            int count = rs.getInt(1);
            Assert.assertEquals("Expected 5 migrations but found " + count, count, 5);
        } catch (SQLException e) {
            Assert.fail("Failed with " + e);
        }
    }

    private void migrationOperation() {
        try {
            Flyway flyway = Flyway
                    .configure()
                    .dataSource(url, "yugabyte", "yugabyte")
                    .locations("filesystem:src/test/resources/db/migration")
                    .load();
            flyway.migrate();
        } catch (FlywayException ex) {
            if (ex.getCause() instanceof SQLException
                    && "40001".equals(((SQLException) ex.getCause()).getSQLState())) {
                // possible, so ignore.
                System.out.println("FlywayException in thread (40001): " + ex);
            } else {
                System.out.println("FlywayException in thread: " + ex);
                errors.put(Thread.currentThread().getName(), ex);
            }
        }
    }

    @After
    public void cleanup() {
        stopYBDBCluster();
    }
}
