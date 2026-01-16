package com.yugabyte;

import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static com.yugabyte.Utils.startYBDBCluster;
import static com.yugabyte.Utils.stopYBDBCluster;

public class TestBaseline {

    static String url = "jdbc:yugabytedb://127.0.0.1:5433/yugabyte";
    Connection conn = null;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        startYBDBCluster();
        Class.forName("com.yugabyte.Driver");
        conn = DriverManager.getConnection(url, "yugabyte", "yugabyte");
    }

    @Test
    public void baselineTest() throws SQLException {
        Flyway flyway = Flyway.configure().dataSource(url, "yugabyte", "yugabyte").load();
        flyway.baseline();

        TestYBLocking.checkMigrations(conn, 1);
    }

    @Test
    public void dlabsTest() throws SQLException {
        Flyway flyway = Flyway.configure()
          .locations("filesystem:src/test/resources/dlabs-schema")
          .dataSource(url, "yugabyte", "yugabyte")
          .baselineVersion("0")
          .baselineOnMigrate(true)
          .loggers("slf4j")
          .load();
        flyway.migrate();

        TestYBLocking.checkMigrations(conn, 7); // 5 + 1 for baselien @ 0 index
    }

    @After
    public void cleanup() {
        stopYBDBCluster();
    }

}
