package com.fisher.overlay;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class FishOverlayView extends View {
    private Paint paintGreen,paintRed,paintText,paintBg,paintBar;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private boolean active=false;
    private int sw,sh;

    // دورة اللعبة (ثوان)
    private static final long CYCLE_MS=8000;
    private long startTime=0;

    // مراحل الدورة
    // 0-20%: تحضير (أحمر)
    // 20-60%: فرصة متوسطة (أصفر)
    // 60-85%: فرصة ذهبية (أخضر) ← أفضل وقت
    // 85-100%: انتهت (أحمر)

    private final Runnable loop=new Runnable(){
        public void run(){
            if(!active)return;
            invalidate();
            handler.postDelayed(this,50);
        }
    };

    public FishOverlayView(Context c){
        super(c);
        paintGreen=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGreen.setColor(0xFF00FF88);

        paintRed=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRed.setColor(0xFFFF4444);

        paintText=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(42f);
        paintText.setFakeBoldText(true);
        paintText.setShadowLayer(6f,2f,2f,Color.BLACK);

        paintBg=new Paint();
        paintBg.setColor(0xCC000000);

        paintBar=new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBar.setColor(0xFF00FF88);
    }

    protected void onSizeChanged(int w,int h,int ow,int oh){
        super.onSizeChanged(w,h,ow,oh);
        sw=w; sh=h;
    }

    public void startUpdateLoop(){
        if(active)return;
        active=true;
        startTime=System.currentTimeMillis();
        handler.post(loop);
    }

    public void stopUpdateLoop(){
        active=false;
        handler.removeCallbacks(loop);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(sw==0||sh==0)return;

        long now=System.currentTimeMillis();
        long elapsed=(now-startTime)%CYCLE_MS;
        float progress=(float)elapsed/CYCLE_MS; // 0.0 -> 1.0

        // تحديد المرحلة
        String label;
        int color;
        String advice;

        if(progress<0.20f){
            label="⏳ انتظر...";
            color=0xFFFF4444;
            advice="الأسماك تتجمع";
        }else if(progress<0.60f){
            label="🟡 فرصة متوسطة";
            color=0xFFFFAA00;
            advice="يمكنك المحاولة";
        }else if(progress<0.85f){
            label="✅ اضغط الآن!";
            color=0xFF00FF88;
            advice="أفضل وقت للصيد!";
        }else{
            label="❌ فاتك الوقت";
            color=0xFFFF4444;
            advice="انتظر الدورة القادمة";
        }

        // خلفية شريط الوقت (أسفل الشاشة)
        float barH=120f;
        float barY=sh-barH-40f;
        canvas.drawRect(20f,barY,sw-20f,barY+barH,paintBg);

        // شريط التقدم
        paintBar.setColor(color);
        float barWidth=(sw-60f)*progress;
        canvas.drawRect(30f,barY+10f,30f+barWidth,barY+barH-10f,paintBar);

        // نص الحالة
        paintText.setColor(color);
        paintText.setTextSize(44f);
        float tw=paintText.measureText(label);
        canvas.drawText(label,(sw-tw)/2f,barY-20f,paintText);

        // نص النصيحة
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(32f);
        float aw=paintText.measureText(advice);
        canvas.drawRect((sw-aw)/2f-16f,barY+barH+10f,
            (sw+aw)/2f+16f,barY+barH+60f,paintBg);
        canvas.drawText(advice,(sw-aw)/2f,barY+barH+50f,paintText);

        // مؤشر دائري في المنتصف
        if(progress>=0.60f&&progress<0.85f){
            paintGreen.setStyle(Paint.Style.STROKE);
            paintGreen.setStrokeWidth(8f);
            float pulse=(float)Math.sin((now%500)/500.0*Math.PI*2)*20f;
            canvas.drawCircle(sw/2f,sh/2f,80f+pulse,paintGreen);
            paintText.setColor(0xFF00FF88);
            paintText.setTextSize(38f);
            String s="اضغط!";
            float sw2=paintText.measureText(s);
            canvas.drawText(s,(sw-sw2)/2f,sh/2f+14f,paintText);
        }

        // عداد الدورة
        long remaining=(long)(CYCLE_MS*(1f-progress)/1000f);
        paintText.setColor(0xFFAAAAAA);
        paintText.setTextSize(26f);
        String timer="دورة جديدة بعد: "+remaining+"ث";
        float ttw=paintText.measureText(timer);
        canvas.drawRect((sw-ttw)/2f-10f,30f,(sw+ttw)/2f+10f,75f,paintBg);
        canvas.drawText(timer,(sw-ttw)/2f,65f,paintText);
    }

    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        stopUpdateLoop();
    }
}
