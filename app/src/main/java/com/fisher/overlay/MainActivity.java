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
    private boolean running=false;
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
                        .setMessage("اضغط السماح ثم فعّل الخيار")
                        .setPositiveButton("فتح الإعدادات",(d,w)->{
                            Intent i=new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:"+getPackageName()));
                            startActivityForResult(i,REQ);
                        })
                        .setNegativeButton("إلغاء",null)
                        .show();
                }else{
                    startApp();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                running=false;
                stopService(new Intent(MainActivity.this,OverlayService.class));
                tvStatus.setText(getString(R.string.status_stopped));
                tvStatus.setTextColor(0xFFAAAAAA);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            }
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
               Settings.canDrawOverlays(this)){
                startApp();
            }else{
                Toast.makeText(this,"لم يتم منح الصلاحية",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void startApp(){
        running=true;
        Intent i=new Intent(this,OverlayService.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            startForegroundService(i);
        else
            startService(i);
        tvStatus.setText(getString(R.string.status_running));
        tvStatus.setTextColor(0xFF00FF88);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        Toast.makeText(this,getString(R.string.analysis_started),Toast.LENGTH_SHORT).show();
    }
    private void updateUI(){
        boolean hasPerm=Build.VERSION.SDK_INT<Build.VERSION_CODES.M||
                        Settings.canDrawOverlays(this);
        if(running){
            tvStatus.setText(getString(R.string.status_running));
            tvStatus.setTextColor(0xFF00FF88);
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        }else{
            tvStatus.setText(hasPerm?getString(R.string.status_stopped):
                "⚠ يجب منح صلاحية العرض");
            tvStatus.setTextColor(hasPerm?0xFFAAAAAA:0xFFFF6600);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        }
    }
}
