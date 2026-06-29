package com.fisher.overlay;
import android.app.Activity;
import android.app.AlertDialog;
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
    private static final int REQ=1001;
    private Button btnStart,btnStop;
    private TextView tvStatus;
    private static FishOverlayView overlayView;
    private static android.view.WindowManager wm;
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        btnStart=(Button)findViewById(R.id.btnStart);
        btnStop=(Button)findViewById(R.id.btnStop);
        tvStatus=(TextView)findViewById(R.id.tvStatus);
        btnStart.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&
                   !Settings.canDrawOverlays(MainActivity.this)){
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("صلاحية مطلوبة")
                        .setMessage("اضغط السماح لتفعيل العرض فوق التطبيقات")
                        .setPositiveButton("فتح الإعدادات",(d,w2)->{
                            startActivityForResult(new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:"+getPackageName())),REQ);
                        })
                        .setNegativeButton("إلغاء",null).show();
                }else{
                    startOverlay();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ stopOverlay(); }
        });
        updateUI();
    }
    protected void onResume(){
        super.onResume();
        updateUI();
    }
    protected void onActivityResult(int req,int res,Intent data){
        super.onActivityResult(req,res,data);
        if(req==REQ){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&
               Settings.canDrawOverlays(this)) startOverlay();
            else Toast.makeText(this,"لم يتم منح الصلاحية",Toast.LENGTH_LONG).show();
        }
    }
    private void startOverlay(){
        try{
            if(overlayView!=null) return;
            wm=(android.view.WindowManager)getSystemService(WINDOW_SERVICE);
            overlayView=new FishOverlayView(getApplicationContext());
            int type=Build.VERSION.SDK_INT>=Build.VERSION_CODES.O?
                android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:
                android.view.WindowManager.LayoutParams.TYPE_PHONE;
            android.view.WindowManager.LayoutParams p=
                new android.view.WindowManager.LayoutParams(
                    android.view.WindowManager.LayoutParams.MATCH_PARENT,
                    android.view.WindowManager.LayoutParams.MATCH_PARENT,
                    type,
                    android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                    android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
                    android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    android.graphics.PixelFormat.TRANSLUCENT);
            wm.addView(overlayView,p);
            overlayView.startUpdateLoop();
            tvStatus.setText(getString(R.string.status_running));
            tvStatus.setTextColor(0xFF00FF88);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            Toast.makeText(this,getString(R.string.analysis_started),Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(this,"خطأ: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void stopOverlay(){
        try{
            if(overlayView!=null&&wm!=null) wm.removeView(overlayView);
        }catch(Exception e){}
        overlayView=null;
        tvStatus.setText(getString(R.string.status_stopped));
        tvStatus.setTextColor(0xFFAAAAAA);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
    }
    private void updateUI(){
        boolean hasPerm=Build.VERSION.SDK_INT<Build.VERSION_CODES.M||
                        Settings.canDrawOverlays(this);
        if(overlayView!=null){
            tvStatus.setText(getString(R.string.status_running));
            tvStatus.setTextColor(0xFF00FF88);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        }else{
            tvStatus.setText(hasPerm?getString(R.string.status_stopped):"⚠ يجب منح صلاحية العرض");
            tvStatus.setTextColor(hasPerm?0xFFAAAAAA:0xFFFF6600);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        }
    }
}
