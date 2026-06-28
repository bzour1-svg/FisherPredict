package com.fisher.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FishOverlayView extends View {

    private static final long UPDATE_INTERVAL_MS = 33;
    private static final int  MAX_FISH_COUNT      = 8;
    private static final long SPAWN_INTERVAL_MS   = 2000;

    private Paint paintCatchable, paintUncatchable, paintText, paintArrow, paintPulse;
    private final List<TrackedFish> trackedFish = new ArrayList<>();
    private final Random random = new Random();
    private int screenWidth, screenHeight;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isActive = false;
    private long lastUpdateTime = 0, lastSpawnTime = 0;

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isActive) return;
            long now = System.currentTimeMillis();
            float delta = (lastUpdateTime > 0) ? (now - lastUpdateTime) : UPDATE_INTERVAL_MS;
            lastUpdateTime = now;
            updateFishPositions(delta);
            if (now - lastSpawnTime > SPAWN_INTERVAL_MS && trackedFish.size() < MAX_FISH_COUNT) {
                spawnFish();
                lastSpawnTime = now;
            }
            invalidate();
            handler.postDelayed(this, UPDATE_INTERVAL_MS);
        }
    };

    public FishOverlayView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paintCatchable = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCatchable.setStyle(Paint.Style.STROKE);
        paintCatchable.setColor(Color.parseColor("#00FF88"));
        paintCatchable.setStrokeWidth(5f);

        paintUncatchable = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintUncatchable.setStyle(Paint.Style.STROKE);
        paintUncatchable.setColor(Color.parseColor("#FF4444"));
        paintUncatchable.setStrokeWidth(2.5f);
        paintUncatchable.setPathEffect(new DashPathEffect(new float[]{8f, 6f}, 0));

        paintText = ne
