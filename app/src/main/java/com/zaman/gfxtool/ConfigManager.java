package com.zaman.gfxtool;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class ConfigManager {
    private static final String TAG = "ZamanGFX";
    private static final String[] CODM_CONFIG_PATHS = {
        "/sdcard/Android/data/com.activision.callofduty.shooter/files/",
        "/sdcard/Android/data/com.garena.game.codm/files/",
        "/sdcard/Android/data/com.tencent.ig/files/"
    };
    private static final int[] FPS_VALUES = {30, 60, 90, 120};
    private static final int[] RESOLUTION_WIDTH = {960, 1280, 1600, 1920, 2560};
    private static final int[] RESOLUTION_HEIGHT = {540, 720, 900, 1080, 1440};
    private static final String[] TEXTURE_LEVELS = {"low","medium","high","veryhigh","max"};
    private static final String[] SHADOW_LEVELS = {"disable","low","medium","high"};
    private static final String[] AA_LEVELS = {"disable","2x","4x"};
    public static boolean writeConfig(Context ctx, int fpsIdx, int resIdx, int texIdx,
            int shadowIdx, int aaIdx, boolean motionBlur, boolean batterySaver,
            boolean antiThrottle, boolean networkOpt, int sensitivity, int screenScale) {
        int fps = FPS_VALUES[fpsIdx];
        int width = RESOLUTION_WIDTH[resIdx];
        int height = RESOLUTION_HEIGHT[resIdx];
        String tex = TEXTURE_LEVELS[texIdx];
        String shadow = SHADOW_LEVELS[shadowIdx];
        String aa = AA_LEVELS[aaIdx];
        String userConfig = "[Graphics]\nMaxFPS=" + fps + "\nScreenWidth=" + width +
            "\nScreenHeight=" + height + "\nTextureQuality=" + tex + "\nShadowQuality=" + shadow +
            "\nAntiAliasing=" + aa + "\nMotionBlur=" + (motionBlur?"1":"0") +
            "\nScreenScale=" + screenScale + "\n[Performance]\nBatterySaverMode=" +
            (batterySaver?"1":"0") + "\nAntiThrottleMode=" + (antiThrottle?"1":"0") +
            "\n[Network]\nNetworkOptimization=" + (networkOpt?"1":"0") +
            "\nSocketBufferSize=" + (networkOpt?"131072":"65536") +
            "\n[Controls]\nSensitivityScale=" + sensitivity +
            "\n[Generated]\nGeneratedBy=ZamanGFXTool\nTimestamp=" +
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) + "\n";
        boolean wrote = false;
        for (String basePath : CODM_CONFIG_PATHS) {
            File dir = new File(basePath);
            if (dir.exists()) {
                wrote |= writeFile(new File(basePath + "UserConfig.ini"), userConfig);
            }
        }
        writeFile(new File(ctx.getFilesDir(), "UserConfig.ini"), userConfig);
        return wrote;
    }
    private static boolean writeFile(File file, String content) {
        try {
            file.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.close();
            return true;
        } catch (IOException e) {
            Log.w(TAG, "Cannot write " + file.getAbsolutePath());
            return false;
        }
    }
    public static void backup(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("ZamanGFX", Context.MODE_PRIVATE);
        SharedPreferences.Editor bkup = ctx.getSharedPreferences("ZamanGFX_Backup", Context.MODE_PRIVATE).edit();
        for (java.util.Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Integer) bkup.putInt(entry.getKey(), (Integer) val);
            if (val instanceof Boolean) bkup.putBoolean(entry.getKey(), (Boolean) val);
            if (val instanceof String) bkup.putString(entry.getKey(), (String) val);
        }
        bkup.apply();
    }
    public static void restore(Context ctx) {
        SharedPreferences bkup = ctx.getSharedPreferences("ZamanGFX_Backup", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ctx.getSharedPreferences("ZamanGFX", Context.MODE_PRIVATE).edit();
        for (java.util.Map.Entry<String, ?> entry : bkup.getAll().entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Integer) editor.putInt(entry.getKey(), (Integer) val);
            if (val instanceof Boolean) editor.putBoolean(entry.getKey(), (Boolean) val);
            if (val instanceof String) editor.putString(entry.getKey(), (String) val);
        }
        editor.apply();
    }
}
