package com.yugabyte;

import java.util.concurrent.TimeUnit;

public class Utils {
    static String path = System.getenv("YBDB_PATH");

    protected static void startYBDBCluster() {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("No valid path available for YBDB_PATH: " + path);
        }
        executeCmd(path + "/bin/yb-ctl destroy", "Stop YugabyteDB cluster", 10);
        executeCmd(path + "/bin/yb-ctl start", "Start YugabyteDB cluster", 120);
    }

    protected static void stopYBDBCluster() {
        executeCmd(path + "/bin/yb-ctl destroy", "Stop YugabyteDB cluster", 10);
    }

    protected static void executeCmd(String cmd, String msg, int timeout) {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("sh", "-c", cmd);
            Process process = builder.start();
            process.waitFor(timeout, TimeUnit.SECONDS);
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException(msg + ": FAILED");
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {}
            System.out.println(msg + ": SUCCEEDED!");
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }
}
