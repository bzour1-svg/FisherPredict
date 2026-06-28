package com.fisher.overlay;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
public class OverlayService extends Service {
    private static final String CHANNEL_ID="fish_ch";
    private static final int NOTE_ID=1;
    private WindowManager wm;
    private FishOverlayView ov;
    public void onCreate(){
        super.onCreate();
        createChannel();
        startForeground(NOTE_ID,buildNote());
        try{
            wm=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
            ov=new FishOverlayView(this);
            int type=Build.VERSION.SDK_INT>=Build.VERSION_CODES.O?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:
                WindowManager.LayoutParams.TYPE_PHONE;
            WindowManager.LayoutParams p=new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
            p.gravity=Gravity.TOP|Gravity.START;
            wm.addView(ov,p);
            ov.startUpdateLoop();
        }catch(Exception e){
            stopSelf();
        }
    }
    public int onStartCommand(Intent i,int f,int s){return START_STICKY;}
    public IBinder onBind(Intent i){return null;}
    public void onDestroy(){
        super.onDestroy();
        try{if(ov!=null&&wm!=null)wm.removeView(ov);}catch(Exception e){}
        ov=null;
    }
    private void createChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel c=new NotificationChannel(
                CHANNEL_ID,"Fisher Predict",NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(c);
        }
    }
    private Notification buildNote(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            return new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle("Fisher Predict")
                .setContentText("يعمل")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setOngoing(true).build();
        return new Notification.Builder(this)
            .setContentTitle("Fisher Predict")
            .setContentText("يعمل")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true).build();
    }
}
