package com.fisher.overlay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int OVERLAY_PERMISSION_CODE = 1001;

    private Button   btnStart;
    private Button   btnStop;
    private TextView tvStatus;
    private boolean  isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button)   findViewById(R.id.btnStart);
        btnStop  = (Button)   findViewById(R.id.btnStop);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasOverlayPermission()) {
                    requestOverlayPermission();
                } else {
                    startOverlay();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopOverlay();
            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private boolean hasOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    private void requestOverlayPermission() {
        Toast.makeText(this,
            getString(R.string.permission_required),
            Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (hasOverlayPermission()) {
                startOverlay();
            } else {
                Toast.makeText(this,
                    getString(R.string.permission_required),
                    Toast.LENGTH_LONG).show();
            }
            updateUI();
        }
    }

    private void startOverlay() {
        if (isRunning) return;
        isRunning = true;
        Intent serviceIntent = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, getString(R.string.analysis_started), Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void stopOverlay() {
        if (!isRunning) return;
        isRunning = false;
        stopService(new Intent(this, OverlayService.class));
        Toast.makeText(this, getString(R.string.analysis_stopped), Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void updateUI() {
        if (isRunning) {
            tvStatus.setText(getString(R.string.status_running));
            tvStatus.setTextColor(0xFF00FF88);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        } else {
            tvStatus.setText(getString(R.string.status_stopped));
            tvStatus.setTextColor(0xFFAAAAAA);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            if (!hasOverlayPermission()) {
                tvStatus.setText("⚠ " + getString(R.string.permission_required));
                tvStatus.setTextColor(0xFFFF6600);
            }
        }
    }
}
