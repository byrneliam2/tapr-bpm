package com.byrneliam2.taprbpm;

import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
    GestureDetector.OnGestureListener {

    private GestureDetectorCompat gd;

    private long totalTime = 0L;
    private long lastTime = 0L;
    private float bpm = 0F;
    private float taps = 0; // needs to be float to avoid integer division

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gd = new GestureDetectorCompat(this, this);

        setTextViewFonts();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // From https://developer.android.com/training/system-ui/immersive.html
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /* ============================================================================= */

    private void calculateBPM(long newTime) {
        totalTime += (60000f/(newTime - lastTime));
        bpm = totalTime/taps;
        lastTime = System.currentTimeMillis();
    }

    private void resetAll() {
        totalTime = lastTime = 0L;
        bpm = taps = 0;
    }

    /* ============================================================================= */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) return false;

        TextView tvb = ((TextView) findViewById(R.id.BPMView));
        TextView tvc = ((TextView) findViewById(R.id.CountView));

        if (lastTime == 0L) {
            lastTime = System.currentTimeMillis();
            tvb.setText(getString(R.string.first_beat));
        } else {
            calculateBPM(System.currentTimeMillis());
            tvb.setText(String.format(Locale.getDefault(), "%.1f", bpm));
        }

        tvc.setText(String.format(Locale.getDefault(), "%.0f", ++taps));

        return true;
    }

    @Override public void onShowPress(MotionEvent motionEvent) {
    }
    @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }
    @Override public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        resetAll();

        TextView tvc = ((TextView) findViewById(R.id.CountView));

        tvc.setText(String.format(Locale.getDefault(), "%.0f", taps));
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /* ============================================================================= */

    private void setTextViewFonts() {
        // Horrible way to achieve this, but we don't have many views to work with here, so it's okay...?
        int[] views = {R.id.BPMView, R.id.CountView};
        for (int view : views) {
            TextView tv = (TextView) findViewById(view);
            tv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));
        }
    }
}
