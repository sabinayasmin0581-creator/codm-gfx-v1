package com.zaman.gfxtool;
import android.app.*;
import android.content.Intent;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.core.app.NotificationCompat;
public class FloatingService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;
    private Handler handler = new Handler();
    private int fakeFps = 60, fakePing = 40;
    private Runnable updateRunnable = new Runnable() {
        public void run() { updateOverlay(); handler.postDelayed(this, 1000); }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundNotification();
        showFloatingOverlay();
    }
    private void startForegroundNotification() {
        String channelId = "zaman_gfx_overlay";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "GFX Overlay", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
        startForeground(1, new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Zaman GFX Tool Active")
                .setContentText("FPS & Ping overlay running")
                .setSmallIcon(android.R.drawable.ic_dialog_info).build());
    }
    private void showFloatingOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_fps, null);
        int layoutFlag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0; params.y = 100;
        windowManager.addView(floatingView, params);
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x; initialY = params.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY(); return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int)(event.getRawX() - initialTouchX);
                        params.y = initialY + (int)(event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params); return true;
                }
                return false;
            }
        });
        handler.post(updateRunnable);
    }
    private void updateOverlay() {
        if (floatingView == null) return;
        TextView tvFps = floatingView.findViewById(R.id.tvOverlayFPS);
        TextView tvPing = floatingView.findViewById(R.id.tvOverlayPing);
        fakeFps = 55 + (int)(Math.random() * 10);
        fakePing = 30 + (int)(Math.random() * 25);
        tvFps.setText(fakeFps + " FPS");
        tvPing.setText(fakePing + " ms");
        tvFps.setTextColor(fakeFps >= 60 ? Color.GREEN : fakeFps >= 45 ? Color.YELLOW : Color.RED);
        tvPing.setTextColor(fakePing <= 50 ? Color.GREEN : fakePing <= 100 ? Color.YELLOW : Color.RED);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
        if (floatingView != null) windowManager.removeView(floatingView);
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
