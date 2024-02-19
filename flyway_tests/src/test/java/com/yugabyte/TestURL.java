package com.yugabyte;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static com.yugabyte.Utils.startYBDBCluster;

public class TestURL {
    static String url = "jdbc:yugabytedb://127.0.0.1:5433/yugabyte";
    Connection conn = null;

    @Before
    public void setup(){
        startYBDBCluster();
    }

    @Test
    public void baselineTest() {
        try{
            Flyway flyway = Flyway.configure().dataSource(url, "yugabyte", "yugabyte").load();
            flyway.migrate();
        }catch (Exception ex){
            throw new RuntimeException("Cannot create connection with protocol jdbc:yugabytedb://", ex);
        }

    }
}
