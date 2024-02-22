package com.yugabyte;

import java.util.concurrent.TimeUnit;

public class Utils {
    static String path = System.getenv("YBDB_PATH");

    protected static void startYBDBCluster() {
        executeCmd(path + "/bin/yugabyted destroy", "Stop YugabyteDB cluster", 10);
        executeCmd(path + "/bin/yugabyted start", "Start YugabyteDB rf=3 cluster", 15);
    }

    protected static void stopYBDBCluster() {
        executeCmd(path + "/bin/yugabyted destroy", "Stop YugabyteDB cluster", 10);
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
            System.out.println(msg + ": SUCCEEDED!");
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }
}
