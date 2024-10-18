package com.fazziclay.fclaypersonstatusclient.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OsCommandExecutor {
    public static String execute(String... args) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(args);

        var stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            sb.append(s).append("\n");
        }

        while ((s = stdError.readLine()) != null) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
