package com.yugabyte;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static com.yugabyte.Utils.startYBDBCluster;
import static com.yugabyte.Utils.stopYBDBCluster;

public class TestAdvisoryLock {
    static String url = "jdbc:yugabytedb://127.0.0.1:5433/yugabyte";
    Connection conn = null;

    @Before
    public void setup() {
        startYBDBCluster();
    }

    @Test
    public void advisoryLockTest() {
        try {
            Flyway flyway = Flyway.configure().dataSource(url, "yugabyte", "yugabyte").load();
            flyway.migrate();
        } catch (FlywayException ex) {
            throw new RuntimeException(ex);
        }
    }

    @After
    public void cleanup() {
        stopYBDBCluster();
    }
}
