package com.example.demo;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

public class URLHelper {
    public static void main(String[] args) {
        createExperimenteeURL("/manager/add_stage", Map.of("username", "shahar", "exp_id", 5, "stage", Map.of("type", "info", "info", "fuck shit shit shit!!!")));
    }

    private static void putOnClipboard(String s) {
        StringSelection stringSelection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void createExperimenteeURL(String path, Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://localhost:8080").append(path);
        if (map.size() > 0) {
            sb.append('?');
            for (String key : map.keySet()) {
                sb.append(key).append('=').append(map.get(key)).append('&');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        String res = sb.toString();
        System.out.println(res);
        putOnClipboard(res);
    }
}
