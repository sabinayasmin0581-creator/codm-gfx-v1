package com.zaman.gfxtool;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.*;

public class ScreenManager {

    // 0 = 6.5", 1 = 6.8", 2 = 8.3", 3 = 13"
    public static void applyScreenSize(Context context, int selectedIndex) {
        SharedPreferences prefs = context.getSharedPreferences("codm_gfx", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String screenSizeValue;
        String dpiValue;

        switch (selectedIndex) {
            case 0:
                screenSizeValue = "6.5";
                dpiValue = "400";
                break;
            case 2:
                screenSizeValue = "8.3";
                dpiValue = "320";
                break;
            case 3:
                screenSizeValue = "13.0";
                dpiValue = "240";
                break;
            default:
                screenSizeValue = "6.8";
                dpiValue = "388";
                break;
        }

        editor.putString("screenSizeValue", screenSizeValue);
        editor.putString("dpiValue", dpiValue);
        editor.apply();

        writeToConfig(screenSizeValue, dpiValue);
    }

    private static void writeToConfig(String screenSize, String dpi) {
        try {
            String configPath = "/sdcard/Android/data/com.activision.callofduty.shooter/files/userdata/user_custom_config.cfg";
            File file = new File(configPath);

            // If file doesnt exist, create it with default content
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean screenSizeFound = false;
            boolean dpiFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("seta r_screenSizeInch")) {
                    sb.append("seta r_screenSizeInch \"").append(screenSize).append("\"\n");
                    screenSizeFound = true;
                } else if (line.startsWith("seta r_displayDPI")) {
                    sb.append("seta r_displayDPI \"").append(dpi).append("\"\n");
                    dpiFound = true;
                } else {
                    sb.append(line).append("\n");
                }
            }
            reader.close();

            // If keys didnt exist, append them
            if (!screenSizeFound) sb.append("seta r_screenSizeInch \"").append(screenSize).append("\"\n");
            if (!dpiFound) sb.append("seta r_displayDPI \"").append(dpi).append("\"\n");

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(sb.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
