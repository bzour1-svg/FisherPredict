package com.fisher.overlay;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class FishOverlayView extends View {
    private Paint paintGreen,paintRed,paintText;
    private final List<TrackedFish> fish=new ArrayList<>();
    private int sw,sh;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private boolean active=false;
    private long lastUpdate=0,lastSpawn=0;
    private final Runnable loop=new Runnable(){
        public void run(){
            if(!active)return;
            long now=System.currentTimeMillis();
            float dt=(lastUpdate>0)?(now-lastUpdate):33;
            lastUpdate=now;
            Iterator<TrackedFish> it=fish.iterator();
            while(it.hasNext()){
                TrackedFish f=it.next();
                f.update(sw,sh,dt);
                if(f.isDead())it.remove();
            }
            if(now-lastSpawn>2000&&fish.size()<8){
                if(sw>0&&sh>0)fish.add(new TrackedFish(sw,sh));
                lastSpawn=now;
            }
            invalidate();
            handler.postDelayed(this,33);
        }
    };
    public FishOverlayView(Context c){
        super(c);
        paintGreen=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGreen.setStyle(Paint.Style.STROKE);
        paintGreen.setColor(0xFF00FF88);
        paintGreen.setStrokeWidth(5f);
        paintRed=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setColor(0xFFFF4444);
        paintRed.setStrokeWidth(2.5f);
        paintText=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(30f);
        paintText.setShadowLayer(4f,1f,1f,Color.BLACK);
    }
    protected void onSizeChanged(int w,int h,int ow,int oh){
        super.onSizeChanged(w,h,ow,oh);
        sw=w;sh=h;
    }
    public void startUpdateLoop(){
        if(active)return;
        active=true;
        lastUpdate=System.currentTimeMillis();
        lastSpawn=System.currentTimeMillis();
        for(int i=0;i<3;i++)if(sw>0&&sh>0)fish.add(new TrackedFish(sw,sh));
        handler.post(loop);
    }
    public void stopUpdateLoop(){
        active=false;
        handler.removeCallbacks(loop);
        fish.clear();
    }
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int catchable=0;
        for(TrackedFish f:fish){
            Paint p=f.isCatchable?paintGreen:paintRed;
            p.setColor(f.getPaintColor());
            p.setAlpha(f.isCatchable?220:130);
            canvas.drawCircle(f.x,f.y,f.radius,p);
            paintText.setTextSize(26f);
            paintText.setColor(f.isCatchable?0xFF00FF88:0xFFFF6666);
            String label=f.isCatchable?"✓ "+f.name:f.name;
            float tw=paintText.measureText(label);
            canvas.drawText(label,f.x-tw/2f,f.y-f.radius-10f,paintText);
            if(f.isCatchable)catchable++;
        }
        paintText.setTextSize(28f);
        paintText.setColor(Color.WHITE);
        String info="اسماك: "+fish.size()+" | قابلة: "+catchable;
        float tw=paintText.measureText(info);
        Paint bg=new Paint();
        bg.setColor(0xAA000000);
        canvas.drawRect(sw/2f-tw/2f-10f,5f,sw/2f+tw/2f+10f,55f,bg);
        canvas.drawText(info,sw/2f-tw/2f,45f,paintText);
    }
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        stopUpdateLoop();
    }
}
