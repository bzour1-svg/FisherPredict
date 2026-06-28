package com.fisher.overlay;
import java.util.Random;
public class TrackedFish {
    public static final String[][] FISH_TYPES = {
        {"قرش","red","fast","40"},
        {"سمكة شريرة","red","medium","30"},
        {"السمكة الحمراء","red","medium","10"},
        {"قنديل البحر","blue","slow","5"},
        {"تمساح","green","medium","12"},
        {"سلحفاة البحر","green","very_slow","8"},
        {"القرش الذهبي","golden","fast","40"},
        {"الدولفين الذهبي","golden","fast","40"},
        {"السلحفاة الذهبية","golden","very_slow","8"},
        {"ملك التنين","purple","medium","40"},
    };
    public float x,y,vx,vy,radius,lifetime;
    public String name,color,speedClass;
    public int value;
    public boolean isCatchable;
    private long catchableChangeTime;
    private static final Random random=new Random();
    public TrackedFish(int sw,int sh){
        String[] t=FISH_TYPES[random.nextInt(FISH_TYPES.length)];
        name=t[0];color=t[1];speedClass=t[2];value=Integer.parseInt(t[3]);
        float m=80f;
        x=m+random.nextFloat()*(sw-m*2);
        y=m+random.nextFloat()*(sh-m*2);
        radius=20f+(value/40f)*30f;
        float s=getBaseSpeed();
        float a=random.nextFloat()*(float)(2*Math.PI);
        vx=(float)(Math.cos(a)*s);
        vy=(float)(Math.sin(a)*s);
        lifetime=5000f+random.nextFloat()*10000f;
        catchableChangeTime=System.currentTimeMillis();
        updateCatchability();
    }
    private float getBaseSpeed(){
        switch(speedClass){
            case "fast": return 4f+random.nextFloat()*2f;
            case "medium": return 2f+random.nextFloat()*1.5f;
            case "slow": return 0.8f+random.nextFloat()*0.8f;
            case "very_slow": return 0.3f+random.nextFloat()*0.4f;
            default: return 1.5f;
        }
    }
    public void updateCatchability(){
        long now=System.currentTimeMillis();
        if(now-catchableChangeTime>2000+random.nextInt(4000)){
            float c;
            switch(speedClass){
                case "very_slow": c=0.80f; break;
                case "slow": c=0.60f; break;
                case "medium": c=0.40f; break;
                case "fast": c=0.20f; break;
                default: c=0.40f;
            }
            if("golden".equals(color)) c*=0.6f;
            isCatchable=random.nextFloat()<c;
            catchableChangeTime=now;
        }
    }
    public void update(int sw,int sh,float dt){
        x+=vx; y+=vy;
        if(x-radius<0){x=radius;vx=Math.abs(vx);}
        else if(x+radius>sw){x=sw-radius;vx=-Math.abs(vx);}
        if(y-radius<0){y=radius;vy=Math.abs(vy);}
        else if(y+radius>sh){y=sh-radius;vy=-Math.abs(vy);}
        lifetime-=dt;
        updateCatchability();
    }
    public boolean isDead(){return lifetime<=0;}
    public int getPaintColor(){
        switch(color){
            case "red": return 0xFFFF4444;
            case "green": return 0xFF44FF44;
            case "blue": return 0xFF4488FF;
            case "golden": return 0xFFFFD700;
            case "purple": return 0xFFBB44FF;
            default: return 0xFFFFFFFF;
        }
    }
}
