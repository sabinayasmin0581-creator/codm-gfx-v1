package com.zaman.gfxtool;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Spinner spinnerFPS, spinnerResolution, spinnerTexture, spinnerShadow, spinnerAntiAlias;
    Switch switchMotionBlur, switchBatterySaver, switchFloatingOverlay, switchAntiThrottle, switchNetworkOpt;
    SeekBar seekbarSensitivity, seekbarScreenScale;
    TextView tvSensValue, tvScreenScaleValue;
    Button btnApply, btnLaunchCODM, btnBackupConfig, btnRestoreConfig;
    RadioGroup rgProfiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("ZamanGFX", MODE_PRIVATE);
        editor = prefs.edit();
        initViews();
        loadSavedSettings();
        setupListeners();
    }
    private void initViews() {
        spinnerFPS = findViewById(R.id.spinnerFPS);
        spinnerResolution = findViewById(R.id.spinnerResolution);
        spinnerTexture = findViewById(R.id.spinnerTexture);
        spinnerShadow = findViewById(R.id.spinnerShadow);
        spinnerAntiAlias = findViewById(R.id.spinnerAntiAlias);
        switchMotionBlur = findViewById(R.id.switchMotionBlur);
        switchBatterySaver = findViewById(R.id.switchBatterySaver);
        switchFloatingOverlay = findViewById(R.id.switchFloatingOverlay);
        switchAntiThrottle = findViewById(R.id.switchAntiThrottle);
        switchNetworkOpt = findViewById(R.id.switchNetworkOpt);
        seekbarSensitivity = findViewById(R.id.seekbarSensitivity);
        seekbarScreenScale = findViewById(R.id.seekbarScreenScale);
        tvSensValue = findViewById(R.id.tvSensValue);
        tvScreenScaleValue = findViewById(R.id.tvScreenScaleValue);
        btnApply = findViewById(R.id.btnApply);
        btnLaunchCODM = findViewById(R.id.btnLaunchCODM);
        btnBackupConfig = findViewById(R.id.btnBackupConfig);
        btnRestoreConfig = findViewById(R.id.btnRestoreConfig);
        rgProfiles = findViewById(R.id.rgProfiles);
        ArrayAdapter<String> fpsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"30 FPS","60 FPS","90 FPS","120 FPS"});
        fpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFPS.setAdapter(fpsAdapter);
        ArrayAdapter<String> resAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Smooth (540p)","Balanced (720p)","HD (900p)","Full HD (1080p)","Ultra HD (1440p)"});
        resAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResolution.setAdapter(resAdapter);
        ArrayAdapter<String> texAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Low","Medium","High","Very High","Max"});
        texAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTexture.setAdapter(texAdapter);
        ArrayAdapter<String> shadowAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Off (Best FPS)","Low","Medium","High"});
        shadowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShadow.setAdapter(shadowAdapter);
        ArrayAdapter<String> aaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Off","2x MSAA","4x MSAA"});
        aaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAntiAlias.setAdapter(aaAdapter);
    }
    private void loadSavedSettings() {
        spinnerFPS.setSelection(prefs.getInt("fps", 1));
        spinnerResolution.setSelection(prefs.getInt("resolution", 1));
        spinnerTexture.setSelection(prefs.getInt("texture", 1));
        spinnerShadow.setSelection(prefs.getInt("shadow", 0));
        spinnerAntiAlias.setSelection(prefs.getInt("aa", 0));
        switchMotionBlur.setChecked(prefs.getBoolean("motionBlur", false));
        switchBatterySaver.setChecked(prefs.getBoolean("batterySaver", false));
        switchFloatingOverlay.setChecked(prefs.getBoolean("floatingOverlay", false));
        switchAntiThrottle.setChecked(prefs.getBoolean("antiThrottle", false));
        switchNetworkOpt.setChecked(prefs.getBoolean("networkOpt", false));
        int sens = prefs.getInt("sensitivity", 50);
        seekbarSensitivity.setProgress(sens);
        tvSensValue.setText(sens + "%");
        int scale = prefs.getInt("screenScale", 100);
        seekbarScreenScale.setProgress(scale - 50);
        tvScreenScaleValue.setText(scale + "%");
    }
    private void setupListeners() {
        seekbarSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) { tvSensValue.setText(progress + "%"); }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });
        seekbarScreenScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) { tvScreenScaleValue.setText((progress + 50) + "%"); }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });
        rgProfiles.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCompetitive) applyProfile("competitive");
            else if (checkedId == R.id.rbStreamer) applyProfile("streamer");
            else if (checkedId == R.id.rbBattery) applyProfile("battery");
            else if (checkedId == R.id.rbBalanced) applyProfile("balanced");
        });
        btnApply.setOnClickListener(v -> applySettings());
        btnLaunchCODM.setOnClickListener(v -> { applySettings(); launchCODM(); });
        btnBackupConfig.setOnClickListener(v -> { ConfigManager.backup(this); Toast.makeText(this, "Config backed up!", Toast.LENGTH_SHORT).show(); });
        btnRestoreConfig.setOnClickListener(v -> { ConfigManager.restore(this); loadSavedSettings(); Toast.makeText(this, "Config restored!", Toast.LENGTH_SHORT).show(); });
        switchFloatingOverlay.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) checkOverlayPermissionAndStart();
            else stopService(new Intent(this, FloatingService.class));
        });
    }
    private void applyProfile(String type) {
        switch (type) {
            case "competitive": spinnerFPS.setSelection(3); spinnerResolution.setSelection(0); spinnerTexture.setSelection(0); spinnerShadow.setSelection(0); spinnerAntiAlias.setSelection(0); switchMotionBlur.setChecked(false); switchAntiThrottle.setChecked(true); switchNetworkOpt.setChecked(true); break;
            case "streamer": spinnerFPS.setSelection(2); spinnerResolution.setSelection(3); spinnerTexture.setSelection(3); spinnerShadow.setSelection(2); spinnerAntiAlias.setSelection(1); switchMotionBlur.setChecked(true); break;
            case "battery": spinnerFPS.setSelection(0); spinnerResolution.setSelection(0); spinnerTexture.setSelection(0); spinnerShadow.setSelection(0); spinnerAntiAlias.setSelection(0); switchMotionBlur.setChecked(false); switchBatterySaver.setChecked(true); break;
            case "balanced": spinnerFPS.setSelection(1); spinnerResolution.setSelection(1); spinnerTexture.setSelection(1); spinnerShadow.setSelection(1); spinnerAntiAlias.setSelection(0); switchMotionBlur.setChecked(false); break;
        }
    }
    private void applySettings() {
        editor.putInt("fps", spinnerFPS.getSelectedItemPosition());
        editor.putInt("resolution", spinnerResolution.getSelectedItemPosition());
        editor.putInt("texture", spinnerTexture.getSelectedItemPosition());
        editor.putInt("shadow", spinnerShadow.getSelectedItemPosition());
        editor.putInt("aa", spinnerAntiAlias.getSelectedItemPosition());
        editor.putBoolean("motionBlur", switchMotionBlur.isChecked());
        editor.putBoolean("batterySaver", switchBatterySaver.isChecked());
        editor.putBoolean("floatingOverlay", switchFloatingOverlay.isChecked());
        editor.putBoolean("antiThrottle", switchAntiThrottle.isChecked());
        editor.putBoolean("networkOpt", switchNetworkOpt.isChecked());
        editor.putInt("sensitivity", seekbarSensitivity.getProgress());
        editor.putInt("screenScale", seekbarScreenScale.getProgress() + 50);
        editor.apply();
        boolean success = ConfigManager.writeConfig(this,
                spinnerFPS.getSelectedItemPosition(), spinnerResolution.getSelectedItemPosition(),
                spinnerTexture.getSelectedItemPosition(), spinnerShadow.getSelectedItemPosition(),
                spinnerAntiAlias.getSelectedItemPosition(), switchMotionBlur.isChecked(),
                switchBatterySaver.isChecked(), switchAntiThrottle.isChecked(),
                switchNetworkOpt.isChecked(), seekbarSensitivity.getProgress(),
                seekbarScreenScale.getProgress() + 50);
        Toast.makeText(this, success ? "✅ Settings applied! Launch CODM now." : "⚠ Settings saved. Launch CODM now.", Toast.LENGTH_SHORT).show();
    }
    private void launchCODM() {
        String[] pkgs = {"com.activision.callofduty.shooter","com.tencent.ig","com.garena.game.codm"};
        for (String pkg : pkgs) {
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            if (i != null) { i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(i); return; }
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.activision.callofduty.shooter")));
    }
    private void checkOverlayPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 100);
        } else {
            startService(new Intent(this, FloatingService.class));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this))
                startService(new Intent(this, FloatingService.class));
            else { switchFloatingOverlay.setChecked(false); Toast.makeText(this, "Overlay permission denied.", Toast.LENGTH_SHORT).show(); }
        }
    }
}
