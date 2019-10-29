package com.meng.monitornotification.nettools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetTools {
    public static Boolean getStatus(String path) {

        HttpURLConnection conn = null;
        URL url = null;
        BufferedReader reader = null;
        StringBuffer sb= new StringBuffer();

        int info = 99;
        try {
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                reader=new BufferedReader(in);
                sb.append(reader.readLine());
                info = Integer.parseInt(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        if (info == 1) {
            return true;
        } else {
            return false;
        }
    }
}
