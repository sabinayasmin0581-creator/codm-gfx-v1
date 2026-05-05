package com.zaman.gfxtool;

import android.content.Context;
import android.content.SharedPreferences;

public class ScreenManager {

    // 0 = 6.5", 1 = 6.8", 2 = 8.3", 3 = 13"
    public static void applyScreenSize(Context context, int selectedIndex) {
        SharedPreferences prefs = context.getSharedPreferences("codm_gfx", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String screenSizeValue;
        String dpiValue;

        switch (selectedIndex) {
            case 0: // 6.5 inch
                screenSizeValue = "6.5";
                dpiValue = "400";
                break;
            case 2: // 8.3 inch
                screenSizeValue = "8.3";
                dpiValue = "320";
                break;
            case 3: // 13 inch
                screenSizeValue = "13.0";
                dpiValue = "240";
                break;
            default: // 6.8 inch
                screenSizeValue = "6.8";
                dpiValue = "388";
                break;
        }

        editor.putString("screenSizeValue", screenSizeValue);
        editor.putString("dpiValue", dpiValue);
        editor.apply();

        // Write to CODM config file
        writeToConfig(screenSizeValue, dpiValue);
    }

    private static void writeToConfig(String screenSize, String dpi) {
        try {
            String configPath = "/sdcard/Android/data/com.activision.callofduty.shooter/files/userdata/user_custom_config.cfg";
            java.io.File file = new java.io.File(configPath);
            if (!file.exists()) return;

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("seta r_customWidth") || line.startsWith("bind r_customWidth")) {
                    // skip old screen lines
                } else if (line.startsWith("seta r_screenSizeInch")) {
                    sb.append("seta r_screenSizeInch \"").append(screenSize).append("\"\n");
                } else if (line.startsWith("seta r_displayDPI")) {
                    sb.append("seta r_displayDPI \"").append(dpi).append("\"\n");
                } else {
                    sb.append(line).append("\n");
                }
            }
            reader.close();

            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file));
            writer.write(sb.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
