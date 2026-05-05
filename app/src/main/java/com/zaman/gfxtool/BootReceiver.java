package com.zaman.gfxtool;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("ZamanGFX", Context.MODE_PRIVATE);
            ConfigManager.writeConfig(context,
                    prefs.getInt("fps", 1), prefs.getInt("resolution", 1),
                    prefs.getInt("texture", 1), prefs.getInt("shadow", 0),
                    prefs.getInt("aa", 0), prefs.getBoolean("motionBlur", false),
                    prefs.getBoolean("batterySaver", false), prefs.getBoolean("antiThrottle", false),
                    prefs.getBoolean("networkOpt", false), prefs.getInt("sensitivity", 50),
                    prefs.getInt("screenScale", 100));
        }
    }
}
