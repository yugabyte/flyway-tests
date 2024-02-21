package com.yugabyte;

import org.flywaydb.core.Flyway;
import org.junit.After;
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

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("select * from flyway_schema_history");

        if(!rs.next()){
            throw new RuntimeException("Baseline Command returned null row from flyway_schema_history table");
        }
    }

    @After
    public void cleanup() {
        stopYBDBCluster();
    }

}
