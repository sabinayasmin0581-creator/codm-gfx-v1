package com.zaman.gfxtool;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.splash_name);
        TextView version = findViewById(R.id.splash_version);
        ScaleAnimation scaleAnim = new ScaleAnimation(0.5f, 1f, 0.5f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(700);
        scaleAnim.setFillAfter(true);
        logo.startAnimation(scaleAnim);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(400);
        fadeIn.setFillAfter(true);
        appName.startAnimation(fadeIn);
        version.startAnimation(fadeIn);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2500);
    }
}
